package com.androidproject.ui.expenses

import androidx.lifecycle.SavedStateHandle
import com.androidproject.data.local.ExpenseEntity
import com.androidproject.ui.groups.GROUP_ID_ARG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddExpenseViewModelTest {

    private val saved = mutableListOf<ExpenseEntity>()
    private val existing = ExpenseEntity("old", "1", "Pizza", 240.0, "me", timestamp = 1_000)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(duplicate: ExpenseEntity?) = AddExpenseViewModel(
        savedStateHandle = SavedStateHandle(mapOf(GROUP_ID_ARG to "1")),
        currentUserId = "me",
        findDuplicate = { duplicate },
        saveExpense = { saved.add(it) },
        now = { 2_000 }
    )

    @Test
    fun noDuplicate_savesDirectly() = runTest {
        val vm = viewModel(duplicate = null)
        vm.save("  Taxi ", 80.0)

        assertEquals(1, saved.size)
        assertEquals("Taxi", saved[0].title)
        assertEquals("1", saved[0].groupId)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun duplicate_showsWarningAndDoesNotSave() = runTest {
        val vm = viewModel(duplicate = existing)
        vm.save("Pizza", 240.0)

        val warning = vm.uiState.value.duplicateWarning
        assertTrue(saved.isEmpty())
        assertEquals(existing, warning?.existingExpense)
        assertEquals(240.0, warning?.newExpense?.amount ?: 0.0, 0.0)
        assertFalse(vm.uiState.value.isSaved)
    }

    @Test
    fun confirmDuplicate_savesTheNewExpense() = runTest {
        val vm = viewModel(duplicate = existing)
        vm.save("Pizza", 240.0)
        val newExpense = vm.uiState.value.duplicateWarning!!.newExpense
        vm.confirmDuplicate()

        assertEquals(listOf(newExpense), saved)
        assertNull(vm.uiState.value.duplicateWarning)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun dismissDuplicate_keepsFormOpenWithoutSaving() = runTest {
        val vm = viewModel(duplicate = existing)
        vm.save("Pizza", 240.0)
        vm.dismissDuplicate()

        assertTrue(saved.isEmpty())
        assertNull(vm.uiState.value.duplicateWarning)
        assertFalse(vm.uiState.value.isSaved)
    }
}

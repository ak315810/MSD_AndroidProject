package com.androidproject.ui.groups

import androidx.lifecycle.SavedStateHandle
import com.androidproject.data.ConnectivityObserver
import com.androidproject.data.local.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroupDetailsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private class FakeConnectivity(initial: Boolean = true) : ConnectivityObserver {
        val online = MutableStateFlow(initial)
        override val isOnline: Flow<Boolean> = online
    }

    private fun entity(id: String, status: String = "SYNCED") =
        ExpenseEntity(id, "1", "Expense $id", 10.0, "u1", syncStatus = status)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        expenses: MutableStateFlow<List<ExpenseEntity>>,
        connectivity: FakeConnectivity = FakeConnectivity()
    ) = GroupDetailsViewModel(
        savedStateHandle = SavedStateHandle(mapOf(GROUP_ID_ARG to "1")),
        getExpenses = { expenses },
        connectivity = connectivity
    )

    // uiState uses WhileSubscribed, so a collector must be active for it to update
    private fun TestScope.viewModel(
        expenses: MutableStateFlow<List<ExpenseEntity>>,
        connectivity: FakeConnectivity = FakeConnectivity()
    ): GroupDetailsViewModel {
        val vm = createViewModel(expenses, connectivity)
        backgroundScope.launch(dispatcher) { vm.uiState.collect {} }
        return vm
    }

    @Test
    fun startsInLoadingState() {
        val vm = createViewModel(MutableStateFlow(emptyList()))
        assertTrue(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.isEmpty)
    }

    @Test
    fun afterFirstEmission_showsExpensesAndPendingCount() = runTest {
        val vm = viewModel(MutableStateFlow(listOf(entity("1"), entity("2", "PENDING"))))
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.expenses.size)
        assertEquals(1, state.pendingCount)
        assertEquals(SyncStatus.PENDING, state.expenses[1].syncStatus)
    }

    @Test
    fun failedExpense_isMappedAndCountedAsNotSynced() = runTest {
        val vm = viewModel(MutableStateFlow(listOf(entity("1", "FAILED"))))
        val state = vm.uiState.value
        assertEquals(SyncStatus.FAILED, state.expenses[0].syncStatus)
        assertEquals(1, state.pendingCount)
    }

    @Test
    fun emptyGroup_isEmpty() = runTest {
        val vm = viewModel(MutableStateFlow(emptyList()))
        assertTrue(vm.uiState.value.isEmpty)
    }

    @Test
    fun connectivityChange_updatesOfflineFlag() = runTest {
        val connectivity = FakeConnectivity()
        val vm = viewModel(MutableStateFlow(emptyList()), connectivity)

        connectivity.online.value = false
        assertTrue(vm.uiState.value.isOffline)
        connectivity.online.value = true
        assertFalse(vm.uiState.value.isOffline)
    }

    @Test
    fun syncCompleting_clearsPendingCount() = runTest {
        val expenses = MutableStateFlow(listOf(entity("1", "PENDING")))
        val vm = viewModel(expenses)
        assertEquals(1, vm.uiState.value.pendingCount)

        expenses.value = listOf(entity("1", "SYNCED"))
        assertEquals(0, vm.uiState.value.pendingCount)
    }
}

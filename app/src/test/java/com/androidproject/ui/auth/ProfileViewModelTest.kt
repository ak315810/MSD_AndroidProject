package com.androidproject.ui.auth

import com.androidproject.R
import com.androidproject.data.AuthRepository
import com.androidproject.data.FakeSessionStorage
import com.androidproject.data.remote.FakeAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var repository: AuthRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = AuthRepository(FakeAuthApi(networkDelayMs = 0), FakeSessionStorage())
        runBlocking { repository.login("demo@student.sdu.dk", "password123") }
        viewModel = ProfileViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun showsLoggedInUser() {
        val state = viewModel.uiState.value
        assertEquals("Demo User", state.name)
        assertEquals("DU", state.initials)
    }

    @Test
    fun editName_updatesNameAndClosesDialog() {
        viewModel.openDialog(ProfileDialog.EDIT_NAME)
        viewModel.updateNameInput("Diego")
        viewModel.saveName()

        val state = viewModel.uiState.value
        assertEquals("Diego", state.name)
        assertEquals(ProfileDialog.NONE, state.openDialog)
        assertEquals(R.string.profile_name_updated, state.message)
    }

    @Test
    fun changePassword_withMismatch_keepsDialogOpen() {
        viewModel.openDialog(ProfileDialog.CHANGE_PASSWORD)
        viewModel.updateCurrentPassword("password123")
        viewModel.updateNewPassword("newpass123")
        viewModel.updateConfirmPassword("newpass124")
        viewModel.changePassword()

        val state = viewModel.uiState.value
        assertEquals(ProfileDialog.CHANGE_PASSWORD, state.openDialog)
        assertEquals(R.string.error_password_mismatch, state.dialogError)
    }

    @Test
    fun changePassword_withWrongCurrent_showsServerError() {
        viewModel.openDialog(ProfileDialog.CHANGE_PASSWORD)
        viewModel.updateCurrentPassword("notmypass1")
        viewModel.updateNewPassword("newpass123")
        viewModel.updateConfirmPassword("newpass123")
        viewModel.changePassword()

        assertEquals(R.string.error_wrong_password, viewModel.uiState.value.dialogError)
    }

    @Test
    fun logout_setsLoggedOutFlag() {
        viewModel.logout()
        assertTrue(viewModel.uiState.value.isLoggedOut)
        assertFalse(repository.isLoggedIn())
    }
}

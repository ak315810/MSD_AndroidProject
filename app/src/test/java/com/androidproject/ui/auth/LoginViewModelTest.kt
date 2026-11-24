package com.androidproject.ui.auth

import com.androidproject.R
import com.androidproject.data.AuthRepository
import com.androidproject.data.FakeSessionStorage
import com.androidproject.data.remote.FakeAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LoginViewModel(AuthRepository(FakeAuthApi(networkDelayMs = 0), FakeSessionStorage()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invalidEmail_showsErrorAndBlocksSubmit() {
        viewModel.updateEmail("demo@")
        viewModel.updatePassword("password123")

        assertEquals(R.string.error_email, viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun emptyEmail_doesNotShowErrorYet() {
        viewModel.updateEmail("")
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun correctCredentials_logIn() {
        viewModel.updateEmail("demo@student.sdu.dk")
        viewModel.updatePassword("password123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertFalse(state.isLoading)
        // password should not stay in memory after a successful login
        assertEquals("", state.password)
    }

    @Test
    fun wrongCredentials_showError() {
        viewModel.updateEmail("demo@student.sdu.dk")
        viewModel.updatePassword("wrongpass1")
        viewModel.login()

        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertEquals(R.string.error_invalid_credentials, state.errorMessage)
    }

    @Test
    fun typingAgain_clearsError() {
        viewModel.updateEmail("demo@student.sdu.dk")
        viewModel.updatePassword("wrongpass1")
        viewModel.login()
        viewModel.updatePassword("password123")

        assertNull(viewModel.uiState.value.errorMessage)
    }
}

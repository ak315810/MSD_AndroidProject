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
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RegisterViewModel(
            AuthRepository(FakeAuthApi(networkDelayMs = 0), FakeSessionStorage())
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fillValidForm(email: String = "new@student.sdu.dk") {
        viewModel.updateName("New User")
        viewModel.updateEmail(email)
        viewModel.updatePassword("password1")
        viewModel.updateConfirmPassword("password1")
    }

    @Test
    fun validForm_canSubmit_andRegisters() {
        fillValidForm()
        assertTrue(viewModel.uiState.value.canSubmit)

        viewModel.register()
        assertTrue(viewModel.uiState.value.isRegistered)
    }

    @Test
    fun weakPassword_showsError() {
        viewModel.updatePassword("abc")
        assertEquals(R.string.error_password, viewModel.uiState.value.passwordError)
    }

    @Test
    fun mismatch_showsError_andClearsWhenPasswordIsFixed() {
        viewModel.updatePassword("password1")
        viewModel.updateConfirmPassword("password2")
        assertEquals(R.string.error_password_mismatch, viewModel.uiState.value.confirmError)

        // changing the first field to match should remove the error as well
        viewModel.updatePassword("password2")
        assertNull(viewModel.uiState.value.confirmError)
    }

    @Test
    fun existingEmail_showsError() {
        fillValidForm(email = "demo@student.sdu.dk")
        viewModel.register()

        val state = viewModel.uiState.value
        assertFalse(state.isRegistered)
        assertEquals(R.string.error_email_taken, state.errorMessage)
    }
}

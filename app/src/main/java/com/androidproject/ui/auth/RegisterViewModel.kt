package com.androidproject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.androidproject.R
import com.androidproject.data.AuthRepository
import com.androidproject.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        val error = if (name.isNotEmpty() && !isValidName(name)) R.string.error_name else null
        _uiState.update { it.copy(name = name, nameError = error, errorMessage = null) }
    }

    fun updateEmail(email: String) {
        val error = if (email.isNotEmpty() && !isValidEmail(email)) R.string.error_email else null
        _uiState.update { it.copy(email = email, emailError = error, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        val error =
            if (password.isNotEmpty() && !isValidPassword(password)) R.string.error_password else null
        _uiState.update {
            it.copy(
                password = password,
                passwordError = error,
                // re-check the confirmation too, otherwise its error gets stuck
                confirmError = confirmErrorFor(password, it.confirmPassword),
                errorMessage = null
            )
        }
    }

    fun updateConfirmPassword(confirmation: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmation,
                confirmError = confirmErrorFor(it.password, confirmation),
                errorMessage = null
            )
        }
    }

    fun register() {
        val state = _uiState.value
        if (!state.canSubmit) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repository.register(state.name, state.email, state.password)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, isRegistered = true)
                }
                is AuthResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.error.toMessage())
                }
            }
        }
    }

    private fun confirmErrorFor(password: String, confirmation: String): Int? =
        if (confirmation.isNotEmpty() && !passwordsMatch(password, confirmation)) {
            R.string.error_password_mismatch
        } else {
            null
        }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegisterViewModel(AuthRepository.getInstance(checkNotNull(this[APPLICATION_KEY])))
            }
        }
    }
}

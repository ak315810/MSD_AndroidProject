package com.androidproject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.androidproject.R
import com.androidproject.data.AuthError
import com.androidproject.data.AuthRepository
import com.androidproject.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        // only complain once the user has typed something (NFR-U2: immediate feedback)
        val error = if (email.isNotEmpty() && !isValidEmail(email)) R.string.error_email else null
        _uiState.update { it.copy(email = email, emailError = error, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        if (!state.canSubmit) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repository.login(state.email, state.password)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, isLoggedIn = true, password = "")
                }
                is AuthResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.error.toMessage())
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel(AuthRepository.getInstance(checkNotNull(this[APPLICATION_KEY])))
            }
        }
    }
}

internal fun AuthError.toMessage(): Int = when (this) {
    AuthError.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
    AuthError.EMAIL_TAKEN -> R.string.error_email_taken
    AuthError.WRONG_PASSWORD -> R.string.error_wrong_password
    AuthError.SESSION_EXPIRED -> R.string.error_session_expired
}

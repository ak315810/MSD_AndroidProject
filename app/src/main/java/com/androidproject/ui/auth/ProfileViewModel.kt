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

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // keep name/email in sync with the repository, e.g. after editing the name
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update {
                    if (user == null) {
                        it.copy(isLoggedOut = true)
                    } else {
                        it.copy(name = user.name, email = user.email)
                    }
                }
            }
        }
    }

    fun openDialog(dialog: ProfileDialog) {
        _uiState.update {
            it.copy(
                openDialog = dialog,
                nameInput = if (dialog == ProfileDialog.EDIT_NAME) it.name else it.nameInput,
                currentPassword = "",
                newPassword = "",
                confirmPassword = "",
                dialogError = null,
                message = null
            )
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(openDialog = ProfileDialog.NONE, dialogError = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun updateNameInput(name: String) {
        _uiState.update { it.copy(nameInput = name, dialogError = null) }
    }

    fun updateCurrentPassword(value: String) {
        _uiState.update { it.copy(currentPassword = value, dialogError = null) }
    }

    fun updateNewPassword(value: String) {
        _uiState.update { it.copy(newPassword = value, dialogError = null) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, dialogError = null) }
    }

    fun saveName() {
        val name = _uiState.value.nameInput.trim()
        if (!isValidName(name)) {
            _uiState.update { it.copy(dialogError = R.string.error_name) }
            return
        }
        runAction(R.string.profile_name_updated) { repository.updateName(name) }
    }

    fun changePassword() {
        val state = _uiState.value
        val error = when {
            state.currentPassword.isEmpty() -> R.string.error_current_password_empty
            !isValidPassword(state.newPassword) -> R.string.error_password
            !passwordsMatch(state.newPassword, state.confirmPassword) ->
                R.string.error_password_mismatch
            state.newPassword == state.currentPassword -> R.string.error_same_password
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(dialogError = error) }
            return
        }
        runAction(R.string.profile_password_changed) {
            repository.changePassword(state.currentPassword, state.newPassword)
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update { it.copy(openDialog = ProfileDialog.NONE, isLoggedOut = true) }
    }

    private fun runAction(successMessage: Int, action: suspend () -> AuthResult) {
        _uiState.update { it.copy(isSaving = true, dialogError = null) }
        viewModelScope.launch {
            when (val result = action()) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        openDialog = ProfileDialog.NONE,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        message = successMessage
                    )
                }
                is AuthResult.Error -> _uiState.update {
                    // expired session is handled by the currentUser collector (isLoggedOut)
                    if (result.error == AuthError.SESSION_EXPIRED) {
                        it.copy(isSaving = false, openDialog = ProfileDialog.NONE)
                    } else {
                        it.copy(isSaving = false, dialogError = result.error.toMessage())
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel(AuthRepository.getInstance(checkNotNull(this[APPLICATION_KEY])))
            }
        }
    }
}

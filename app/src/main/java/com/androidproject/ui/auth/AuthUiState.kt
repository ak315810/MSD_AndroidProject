package com.androidproject.ui.auth

import androidx.annotation.StringRes

// Errors are string resource ids so the ViewModels never need a Context

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val errorMessage: Int? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
) {
    val canSubmit: Boolean
        get() = isValidEmail(email) && password.isNotEmpty() && !isLoading
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmError: Int? = null,
    @StringRes val errorMessage: Int? = null,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false
) {
    val canSubmit: Boolean
        get() = isValidName(name) && isValidEmail(email) && isValidPassword(password) &&
            passwordsMatch(password, confirmPassword) && !isLoading
}

enum class ProfileDialog { NONE, EDIT_NAME, CHANGE_PASSWORD, LOGOUT }

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val openDialog: ProfileDialog = ProfileDialog.NONE,
    val nameInput: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    @StringRes val dialogError: Int? = null,
    @StringRes val message: Int? = null,
    val isSaving: Boolean = false,
    val isLoggedOut: Boolean = false
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}

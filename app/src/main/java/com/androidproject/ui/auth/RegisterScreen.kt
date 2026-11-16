package com.androidproject.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidproject.R
import com.androidproject.ui.theme.AndroidProjectTheme

@Composable
fun RegisterRoute(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) onRegistered()
    }

    RegisterScreen(
        state = state,
        onBack = onBack,
        onNameChange = viewModel::updateName,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onRegister = viewModel::register,
        onLoginClick = onLoginClick
    )
}

// S3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = R.string.name_label,
                error = state.nameError
            )
            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = R.string.email_label,
                error = state.emailError,
                keyboardType = KeyboardType.Email
            )
            PasswordTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = R.string.password_label,
                error = state.passwordError
            )
            // show the rule up front instead of only after a mistake
            if (state.passwordError == null) {
                Text(
                    text = stringResource(R.string.password_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = R.string.confirm_password_label,
                error = state.confirmError,
                imeAction = ImeAction.Done,
                onDone = onRegister
            )

            ErrorText(state.errorMessage)

            LoadingButton(
                text = stringResource(R.string.create_account_button),
                onClick = onRegister,
                enabled = state.canSubmit,
                isLoading = state.isLoading
            )

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.have_account_login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    AndroidProjectTheme {
        RegisterScreen(
            state = RegisterUiState(
                name = "Diego",
                email = "diego@student.sdu.dk",
                password = "abc",
                passwordError = R.string.error_password
            ),
            onBack = {}, onNameChange = {}, onEmailChange = {}, onPasswordChange = {},
            onConfirmPasswordChange = {}, onRegister = {}, onLoginClick = {}
        )
    }
}

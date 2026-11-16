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
fun LoginRoute(
    onBack: () -> Unit,
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    LoginScreen(
        state = state,
        onBack = onBack,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onLogin = viewModel::login,
        onRegisterClick = onRegisterClick
    )
}

// S2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginUiState,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.login_title)) },
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
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                error = null,
                imeAction = ImeAction.Done,
                onDone = onLogin
            )

            ErrorText(state.errorMessage)

            LoadingButton(
                text = stringResource(R.string.login_button),
                onClick = onLogin,
                enabled = state.canSubmit,
                isLoading = state.isLoading
            )

            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.no_account_register))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AndroidProjectTheme {
        LoginScreen(
            state = LoginUiState(email = "demo@student.sdu.dk", password = "secret"),
            onBack = {}, onEmailChange = {}, onPasswordChange = {}, onLogin = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Login - error")
@Composable
private fun LoginScreenErrorPreview() {
    AndroidProjectTheme {
        LoginScreen(
            state = LoginUiState(
                email = "demo@",
                emailError = R.string.error_email,
                errorMessage = R.string.error_invalid_credentials
            ),
            onBack = {}, onEmailChange = {}, onPasswordChange = {}, onLogin = {},
            onRegisterClick = {}
        )
    }
}

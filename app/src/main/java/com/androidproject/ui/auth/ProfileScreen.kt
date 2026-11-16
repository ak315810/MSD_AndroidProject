package com.androidproject.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidproject.R
import com.androidproject.ui.theme.AndroidProjectTheme

@Composable
fun ProfileRoute(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLoggedOut()
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(context.getString(it))
            viewModel.clearMessage()
        }
    }

    ProfileScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onOpenDialog = viewModel::openDialog,
        onDismissDialog = viewModel::dismissDialog,
        onNameInputChange = viewModel::updateNameInput,
        onSaveName = viewModel::saveName,
        onCurrentPasswordChange = viewModel::updateCurrentPassword,
        onNewPasswordChange = viewModel::updateNewPassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onChangePassword = viewModel::changePassword,
        onLogout = viewModel::logout
    )
}

// S7
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onOpenDialog: (ProfileDialog) -> Unit,
    onDismissDialog: () -> Unit,
    onNameInputChange: (String) -> Unit,
    onSaveName: () -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineLarge
            )

            ProfileHeader(
                initials = state.initials,
                name = state.name,
                onEditClick = { onOpenDialog(ProfileDialog.EDIT_NAME) }
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                ProfileRow(
                    icon = Icons.Filled.Email,
                    title = stringResource(R.string.email_label),
                    subtitle = state.email
                )
                HorizontalDivider()
                ProfileRow(
                    icon = Icons.Filled.Lock,
                    title = stringResource(R.string.change_password),
                    onClick = { onOpenDialog(ProfileDialog.CHANGE_PASSWORD) }
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                ProfileRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = stringResource(R.string.logout),
                    isDestructive = true,
                    onClick = { onOpenDialog(ProfileDialog.LOGOUT) }
                )
            }
        }
    }

    when (state.openDialog) {
        ProfileDialog.EDIT_NAME -> EditNameDialog(
            state = state,
            onNameChange = onNameInputChange,
            onSave = onSaveName,
            onDismiss = onDismissDialog
        )
        ProfileDialog.CHANGE_PASSWORD -> ChangePasswordDialog(
            state = state,
            onCurrentChange = onCurrentPasswordChange,
            onNewChange = onNewPasswordChange,
            onConfirmChange = onConfirmPasswordChange,
            onSave = onChangePassword,
            onDismiss = onDismissDialog
        )
        ProfileDialog.LOGOUT -> AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(stringResource(R.string.logout_title)) },
            text = { Text(stringResource(R.string.logout_message)) },
            confirmButton = {
                TextButton(onClick = onLogout) { Text(stringResource(R.string.logout)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel)) }
            }
        )
        ProfileDialog.NONE -> Unit
    }
}

@Composable
private fun ProfileHeader(
    initials: String,
    name: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // no photo upload yet, so the avatar just shows the user's initials
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onEditClick, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_name))
        }
    }
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val color = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val rowModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = color)
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (onClick != null && !isDestructive) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun EditNameDialog(
    state: ProfileUiState,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_name)) },
        text = {
            AuthTextField(
                value = state.nameInput,
                onValueChange = onNameChange,
                label = R.string.name_label,
                error = state.dialogError,
                imeAction = ImeAction.Done
            )
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !state.isSaving) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    state: ProfileUiState,
    onCurrentChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_password)) },
        text = {
            Column {
                PasswordTextField(
                    value = state.currentPassword,
                    onValueChange = onCurrentChange,
                    label = R.string.current_password_label,
                    error = null
                )
                PasswordTextField(
                    value = state.newPassword,
                    onValueChange = onNewChange,
                    label = R.string.new_password_label,
                    error = null
                )
                PasswordTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmChange,
                    label = R.string.confirm_password_label,
                    error = null,
                    imeAction = ImeAction.Done,
                    onDone = onSave
                )
                ErrorText(state.dialogError)
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !state.isSaving) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    AndroidProjectTheme {
        ProfileScreen(
            state = ProfileUiState(name = "Diego Example", email = "diego@student.sdu.dk"),
            onOpenDialog = {}, onDismissDialog = {}, onNameInputChange = {}, onSaveName = {},
            onCurrentPasswordChange = {}, onNewPasswordChange = {}, onConfirmPasswordChange = {},
            onChangePassword = {}, onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile - change password")
@Composable
private fun ProfileChangePasswordPreview() {
    AndroidProjectTheme {
        ProfileScreen(
            state = ProfileUiState(
                name = "Diego Example",
                email = "diego@student.sdu.dk",
                openDialog = ProfileDialog.CHANGE_PASSWORD,
                dialogError = R.string.error_password_mismatch
            ),
            onOpenDialog = {}, onDismissDialog = {}, onNameInputChange = {}, onSaveName = {},
            onCurrentPasswordChange = {}, onNewPasswordChange = {}, onConfirmPasswordChange = {},
            onChangePassword = {}, onLogout = {}
        )
    }
}

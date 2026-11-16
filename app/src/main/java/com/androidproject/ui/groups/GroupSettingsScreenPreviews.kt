package com.androidproject.ui.groups

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.androidproject.ui.theme.AndroidProjectTheme

private val base = GroupSettingsUiState(
    groupName = "Trip to Odense",
    description = "Weekend trip expenses",
    participants = listOf("Person 1", "Person 2", "Person 3")
)

@Composable
private fun PreviewSettings(state: GroupSettingsUiState) {
    AndroidProjectTheme {
        GroupSettingsScreen(
            state = state,
            onBack = {}, onGroupNameChange = {}, onDescriptionChange = {},
            onNewParticipantChange = {}, onAddParticipant = {}, onRemoveParticipant = {},
            onSave = {}, onDeleteGroupClick = {}, onDeleteDismiss = {}, onDeleteConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings")
@Composable
private fun SettingsPreview() = PreviewSettings(base)

@Preview(showBackground = true, name = "Settings - name error")
@Composable
private fun SettingsErrorPreview() = PreviewSettings(base.copy(groupName = "", isNameError = true))

@Preview(showBackground = true, name = "Settings - delete dialog")
@Composable
private fun SettingsDialogPreview() = PreviewSettings(base.copy(showDeleteDialog = true))

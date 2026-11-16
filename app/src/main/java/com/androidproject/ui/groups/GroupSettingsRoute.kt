package com.androidproject.ui.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupSettingsRoute(
    onBack: () -> Unit,
    onGroupDeleted: () -> Unit,
    viewModel: GroupSettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GroupSettingsScreen(
        state = state,
        onBack = onBack,
        onGroupNameChange = viewModel::updateGroupName,
        onDescriptionChange = viewModel::updateDescription,
        onNewParticipantChange = viewModel::updateNewParticipantName,
        onAddParticipant = viewModel::addParticipant,
        onRemoveParticipant = viewModel::removeParticipant,
        onSave = { if (viewModel.saveChanges()) onBack() },
        onDeleteGroupClick = { viewModel.showDeleteDialog(true) },
        onDeleteDismiss = { viewModel.showDeleteDialog(false) },
        onDeleteConfirm = {
            viewModel.deleteGroup()
            onGroupDeleted()
        }
    )
}

package com.androidproject.ui.groups

data class GroupSettingsUiState(
    val groupName: String = "",
    val description: String = "",
    val participants: List<String> = emptyList(),
    val newParticipantName: String = "",
    val showDeleteDialog: Boolean = false,
    val isNameError: Boolean = false
)
package com.androidproject.ui.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GroupSettingsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    @Suppress("unused")
    private val groupId: String = checkNotNull(savedStateHandle.get<String>(GROUP_ID_ARG))

    // TODO: load the real group by groupId (repository) instead of this mock.
    private val _uiState = MutableStateFlow(
        GroupSettingsUiState(
            groupName = "Trip to Odense",
            description = "Weekend trip expenses",
            participants = listOf("Person 1", "Person 2", "Person 3")
        )
    )
    val uiState: StateFlow<GroupSettingsUiState> = _uiState.asStateFlow()

    fun updateGroupName(name: String) {
        _uiState.update { it.copy(groupName = name, isNameError = name.isBlank()) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateNewParticipantName(name: String) {
        _uiState.update { it.copy(newParticipantName = name) }
    }

    fun addParticipant() {
        val name = _uiState.value.newParticipantName.trim()
        if (name.isEmpty()) return
        if (_uiState.value.participants.any { it.equals(name, ignoreCase = true) }) return

        _uiState.update {
            it.copy(participants = it.participants + name, newParticipantName = "")
        }
    }

    fun removeParticipant(participant: String) {
        // TODO: do not allow removing a participant that already has expenses (needs the expenses source)
        _uiState.update { state ->
            state.copy(participants = state.participants.filter { it != participant })
        }
    }

    fun showDeleteDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun saveChanges(): Boolean {
        val trimmedName = _uiState.value.groupName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(groupName = trimmedName, isNameError = true) }
            return false
        }
        _uiState.update { it.copy(groupName = trimmedName, isNameError = false) }
        // TODO: persist local-first through the repository (+ outbox)
        return true
    }

    fun deleteGroup() {
        // TODO: delete / leave through the repository
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
}

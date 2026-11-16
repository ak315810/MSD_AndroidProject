package com.androidproject.ui.groups

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Shared group list, kept in a ViewModel so it survives rotation
class GroupsViewModel : ViewModel() {

    // TODO: replace the mock list with the group repository once the data layer has groups
    private val _groups = MutableStateFlow(
        listOf(
            Group(1, "Erasmus Flat", 4),
            Group(2, "Copenhagen Trip", 6),
            Group(3, "Birthday Party", 8)
        )
    )
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    fun addGroup(name: String) {
        _groups.update { current ->
            val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
            current + Group(id = newId, name = name, members = 1)
        }
    }

    fun removeGroup(groupId: String) {
        _groups.update { current -> current.filterNot { it.id.toString() == groupId } }
    }
}

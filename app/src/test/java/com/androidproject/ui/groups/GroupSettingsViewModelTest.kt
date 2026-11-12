package com.androidproject.ui.groups

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupSettingsViewModelTest {

    private fun newViewModel() =
        GroupSettingsViewModel(SavedStateHandle(mapOf(GROUP_ID_ARG to "1")))

    @Test
    fun blankName_setsError_andIsNotSaved() {
        val viewModel = newViewModel()
        viewModel.updateGroupName("")
        assertTrue(viewModel.uiState.value.isNameError)
        assertFalse(viewModel.saveChanges())
    }

    @Test
    fun validName_clearsError_andIsSaved() {
        val viewModel = newViewModel()
        viewModel.updateGroupName("New name")
        assertFalse(viewModel.uiState.value.isNameError)
        assertTrue(viewModel.saveChanges())
    }

    @Test
    fun save_trimsTheName() {
        val viewModel = newViewModel()
        viewModel.updateGroupName("  Trip  ")
        assertTrue(viewModel.saveChanges())
        assertEquals("Trip", viewModel.uiState.value.groupName)
    }

    @Test
    fun addParticipant_trimsName_andClearsInput() {
        val viewModel = newViewModel()
        val before = viewModel.uiState.value.participants.size
        viewModel.updateNewParticipantName("  Eva ")
        viewModel.addParticipant()

        val state = viewModel.uiState.value
        assertEquals(before + 1, state.participants.size)
        assertEquals("Eva", state.participants.last())
        assertEquals("", state.newParticipantName)
    }

    @Test
    fun addParticipant_ignoresBlankAndDuplicates() {
        val viewModel = newViewModel()
        val before = viewModel.uiState.value.participants.size

        viewModel.updateNewParticipantName("   ")
        viewModel.addParticipant()
        viewModel.updateNewParticipantName("person 1")
        viewModel.addParticipant()

        assertEquals(before, viewModel.uiState.value.participants.size)
    }

    @Test
    fun removeParticipant_removesIt() {
        val viewModel = newViewModel()
        viewModel.removeParticipant("Person 1")
        assertFalse("Person 1" in viewModel.uiState.value.participants)
    }

    @Test
    fun deleteDialog_opensAndCloses() {
        val viewModel = newViewModel()
        viewModel.showDeleteDialog(true)
        assertTrue(viewModel.uiState.value.showDeleteDialog)
        viewModel.deleteGroup()
        assertFalse(viewModel.uiState.value.showDeleteDialog)
    }
}

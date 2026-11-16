package com.androidproject.ui.groups

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GroupsViewModelTest {

    @Test
    fun addGroup_getsNextIdAndOneMember() {
        val viewModel = GroupsViewModel()
        viewModel.addGroup("Ski Trip")

        val added = viewModel.groups.value.last()
        assertEquals(4, added.id)
        assertEquals("Ski Trip", added.name)
        assertEquals(1, added.members)
    }

    @Test
    fun removeGroup_removesById() {
        val viewModel = GroupsViewModel()
        viewModel.removeGroup("2")

        assertFalse(viewModel.groups.value.any { it.id == 2 })
    }
}

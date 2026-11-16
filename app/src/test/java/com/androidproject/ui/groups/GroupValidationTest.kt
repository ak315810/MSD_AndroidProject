
package com.androidproject.ui.groups

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupValidationTest {

    @Test
    fun validGroupName_returnsTrue() {
        val result = isValidGroupName("Erasmus Flat")

        assertTrue(result)
    }

    @Test
    fun emptyGroupName_returnsFalse() {
        val result = isValidGroupName("")

        assertFalse(result)
    }

    @Test
    fun spacesOnly_returnsFalse() {
        val result = isValidGroupName("   ")

        assertFalse(result)
    }

    @Test
    fun anotherValidGroupName_returnsTrue() {
        val result = isValidGroupName("Copenhagen Trip")

        assertTrue(result)
    }
}

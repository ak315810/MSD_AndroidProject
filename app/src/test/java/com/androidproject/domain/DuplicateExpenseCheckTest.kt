package com.androidproject.domain

import com.androidproject.data.local.ExpenseEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateExpenseCheckTest {

    private val hour = 60 * 60 * 1000L
    private val base = ExpenseEntity("1", "g1", "Pizza", 240.0, "maria", timestamp = 10 * hour)

    @Test
    fun sameExpenseAddedTwice_isDuplicate() {
        val new = base.copy(id = "2", title = " pizza ", timestamp = base.timestamp + hour)
        assertTrue(isPossibleDuplicate(new, base))
    }

    @Test
    fun differentAmountPayerOrGroup_isNotDuplicate() {
        assertFalse(isPossibleDuplicate(base.copy(id = "2", amount = 241.0), base))
        assertFalse(isPossibleDuplicate(base.copy(id = "2", paidByUserId = "ania"), base))
        assertFalse(isPossibleDuplicate(base.copy(id = "2", groupId = "g2"), base))
        assertFalse(isPossibleDuplicate(base.copy(id = "2", title = "Taxi"), base))
    }

    @Test
    fun moreThanOneDayApart_isNotDuplicate() {
        val new = base.copy(id = "2", timestamp = base.timestamp + DUPLICATE_WINDOW_MS + 1)
        assertFalse(isPossibleDuplicate(new, base))
    }

    @Test
    fun sameId_isNotDuplicate() {
        assertFalse(isPossibleDuplicate(base, base))
    }

    @Test
    fun findPossibleDuplicate_returnsClosestMatch() {
        val older = base.copy(id = "a", timestamp = base.timestamp - 5 * hour)
        val closer = base.copy(id = "b", timestamp = base.timestamp - hour)
        val new = base.copy(id = "new")
        assertEquals(closer, findPossibleDuplicate(new, listOf(older, closer)))
        assertNull(findPossibleDuplicate(new, emptyList()))
    }
}

package com.androidproject.ui.expenses

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseAmountTest {

    @Test
    fun acceptsDotAndComma() {
        assertEquals(12.5, parseAmount("12.50")!!, 0.0)
        assertEquals(12.5, parseAmount("12,50")!!, 0.0)
    }

    @Test
    fun rejectsZeroAndText() {
        assertNull(parseAmount("0"))
        assertNull(parseAmount("abc"))
        assertNull(parseAmount(""))
    }
}

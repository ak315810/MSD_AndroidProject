package com.androidproject.ui.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationTest {

    @Test
    fun validEmail_isAccepted() {
        assertTrue(isValidEmail("diego@student.sdu.dk"))
        assertTrue(isValidEmail("  first.last+test@gmail.com "))
    }

    @Test
    fun invalidEmail_isRejected() {
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("diego"))
        assertFalse(isValidEmail("diego@"))
        assertFalse(isValidEmail("diego@sdu"))
        assertFalse(isValidEmail("die go@sdu.dk"))
    }

    @Test
    fun password_needsLengthLettersAndDigits() {
        assertTrue(isValidPassword("password1"))
        assertFalse(isValidPassword("pass1"))
        assertFalse(isValidPassword("onlyletters"))
        assertFalse(isValidPassword("12345678"))
    }

    @Test
    fun name_needsTwoCharactersAfterTrim() {
        assertTrue(isValidName("Al"))
        assertFalse(isValidName(" A "))
        assertFalse(isValidName(""))
    }

    @Test
    fun passwordsMatch_rejectsEmptyAndDifferent() {
        assertTrue(passwordsMatch("password1", "password1"))
        assertFalse(passwordsMatch("password1", "password2"))
        assertFalse(passwordsMatch("", ""))
    }
}

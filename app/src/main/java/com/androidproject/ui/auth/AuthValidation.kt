package com.androidproject.ui.auth

const val MIN_PASSWORD_LENGTH = 8

// Kept as plain Kotlin (no android.util.Patterns) so the rules can be unit tested on the JVM
private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

fun isValidPassword(password: String): Boolean =
    password.length >= MIN_PASSWORD_LENGTH && password.any { it.isDigit() } &&
        password.any { it.isLetter() }

fun isValidName(name: String): Boolean = name.trim().length >= 2

fun passwordsMatch(password: String, confirmation: String): Boolean =
    password.isNotEmpty() && password == confirmation

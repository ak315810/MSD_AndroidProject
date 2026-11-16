package com.androidproject.ui.auth

// Kept in one place to avoid typos in route strings (S-IDs from the navigation model)
object AuthRoutes {
    const val GRAPH = "auth"
    const val WELCOME = "welcome"   // S1
    const val LOGIN = "login"       // S2
    const val REGISTER = "register" // S3
    const val PROFILE = "profile"   // S7

    val authFlow = setOf(GRAPH, WELCOME, LOGIN, REGISTER)
}

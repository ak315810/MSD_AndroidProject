package com.androidproject.data.remote

import com.androidproject.domain.User
import kotlinx.coroutines.delay
import java.util.UUID

data class AuthResponse(
    val token: String,
    val user: User
)

class AuthApiException(val reason: Reason) : Exception(reason.name) {
    enum class Reason { INVALID_CREDENTIALS, EMAIL_TAKEN, WRONG_PASSWORD, UNAUTHORIZED }
}

// Stand-in for the real backend until we pick Firestore or a REST API.
// Accounts only live in memory, so they are gone after the app process is killed.
class FakeAuthApi(private val networkDelayMs: Long = 600) {

    private data class Account(val user: User, var password: String)

    private val accounts = mutableMapOf(
        "demo@student.sdu.dk" to Account(
            User(id = "u1", name = "Demo User", email = "demo@student.sdu.dk"),
            password = "password123"
        )
    )

    // token -> email
    private val sessions = mutableMapOf<String, String>()

    suspend fun login(email: String, password: String): AuthResponse {
        delay(networkDelayMs)
        val account = accounts[email.lowercase()]
        if (account == null || account.password != password) {
            throw AuthApiException(AuthApiException.Reason.INVALID_CREDENTIALS)
        }
        return createSession(account.user)
    }

    suspend fun register(name: String, email: String, password: String): AuthResponse {
        delay(networkDelayMs)
        val key = email.lowercase()
        if (accounts.containsKey(key)) {
            throw AuthApiException(AuthApiException.Reason.EMAIL_TAKEN)
        }
        val user = User(id = UUID.randomUUID().toString(), name = name, email = key)
        accounts[key] = Account(user, password)
        return createSession(user)
    }

    suspend fun updateName(token: String, newName: String): User {
        delay(networkDelayMs)
        val account = accountFor(token)
        val updated = account.user.copy(name = newName)
        accounts[updated.email] = account.copy(user = updated)
        return updated
    }

    suspend fun changePassword(token: String, currentPassword: String, newPassword: String) {
        delay(networkDelayMs)
        val account = accountFor(token)
        if (account.password != currentPassword) {
            throw AuthApiException(AuthApiException.Reason.WRONG_PASSWORD)
        }
        account.password = newPassword
    }

    fun logout(token: String) {
        sessions.remove(token)
    }

    private fun createSession(user: User): AuthResponse {
        val token = UUID.randomUUID().toString()
        sessions[token] = user.email
        return AuthResponse(token, user)
    }

    private fun accountFor(token: String): Account {
        val email = sessions[token]
            ?: throw AuthApiException(AuthApiException.Reason.UNAUTHORIZED)
        return accounts[email]
            ?: throw AuthApiException(AuthApiException.Reason.UNAUTHORIZED)
    }
}

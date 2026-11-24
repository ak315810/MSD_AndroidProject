package com.androidproject.data

import android.content.Context
import com.androidproject.data.local.KeystoreSessionStorage
import com.androidproject.data.local.Session
import com.androidproject.data.local.SessionStorage
import com.androidproject.data.remote.AuthApiException
import com.androidproject.data.remote.FakeAuthApi
import com.androidproject.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AuthError { INVALID_CREDENTIALS, EMAIL_TAKEN, WRONG_PASSWORD, SESSION_EXPIRED }

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val error: AuthError) : AuthResult()
}

class AuthRepository(
    private val api: FakeAuthApi,
    private val storage: SessionStorage
) {

    private var session: Session? = storage.load()

    // Shared by every screen that needs the logged-in user (profile, home, groups later on)
    private val _currentUser = MutableStateFlow(session?.toUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun isLoggedIn(): Boolean = _currentUser.value != null

    suspend fun login(email: String, password: String): AuthResult = runCatchingAuth {
        val response = api.login(email.trim(), password)
        startSession(response.token, response.user)
    }

    suspend fun register(name: String, email: String, password: String): AuthResult =
        runCatchingAuth {
            val response = api.register(name.trim(), email.trim(), password)
            startSession(response.token, response.user)
        }

    suspend fun updateName(newName: String): AuthResult = runCatchingAuth {
        val current = session ?: throw AuthApiException(AuthApiException.Reason.UNAUTHORIZED)
        val user = api.updateName(current.token, newName.trim())
        startSession(current.token, user)
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult =
        runCatchingAuth {
            val current = session ?: throw AuthApiException(AuthApiException.Reason.UNAUTHORIZED)
            api.changePassword(current.token, currentPassword, newPassword)
            current.toUser()
        }

    fun logout() {
        session?.let { api.logout(it.token) }
        session = null
        storage.clear()
        _currentUser.value = null
    }

    private fun startSession(token: String, user: User): User {
        val newSession = Session(token, user.id, user.name, user.email)
        storage.save(newSession)
        session = newSession
        _currentUser.value = user
        return user
    }

    private inline fun runCatchingAuth(block: () -> User): AuthResult {
        return try {
            AuthResult.Success(block())
        } catch (e: AuthApiException) {
            val error = when (e.reason) {
                AuthApiException.Reason.INVALID_CREDENTIALS -> AuthError.INVALID_CREDENTIALS
                AuthApiException.Reason.EMAIL_TAKEN -> AuthError.EMAIL_TAKEN
                AuthApiException.Reason.WRONG_PASSWORD -> AuthError.WRONG_PASSWORD
                AuthApiException.Reason.UNAUTHORIZED -> {
                    // token is no longer valid on the server, so the local copy is useless too
                    logout()
                    AuthError.SESSION_EXPIRED
                }
            }
            AuthResult.Error(error)
        }
    }

    private fun Session.toUser() = User(id = userId, name = name, email = email)

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        // One instance for the whole app, same idea as AppDatabase.getDatabase()
        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(
                    FakeAuthApi(),
                    KeystoreSessionStorage(context.applicationContext)
                ).also { INSTANCE = it }
            }
        }
    }
}

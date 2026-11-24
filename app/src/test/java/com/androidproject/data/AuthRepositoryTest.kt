package com.androidproject.data

import com.androidproject.data.local.Session
import com.androidproject.data.remote.FakeAuthApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    private val storage = FakeSessionStorage()
    private val repository = AuthRepository(FakeAuthApi(networkDelayMs = 0), storage)

    @Test
    fun login_withDemoAccount_savesSession() = runTest {
        val result = repository.login("demo@student.sdu.dk", "password123")

        assertTrue(result is AuthResult.Success)
        assertTrue(repository.isLoggedIn())
        assertEquals("demo@student.sdu.dk", storage.saved?.email)
        assertNotNull(storage.saved?.token)
    }

    @Test
    fun login_withWrongPassword_returnsError_andStaysLoggedOut() = runTest {
        val result = repository.login("demo@student.sdu.dk", "wrong")

        assertEquals(AuthResult.Error(AuthError.INVALID_CREDENTIALS), result)
        assertFalse(repository.isLoggedIn())
        assertNull(storage.saved)
    }

    @Test
    fun register_sameEmailTwice_fails() = runTest {
        repository.register("Anna", "anna@student.sdu.dk", "password1")
        val second = repository.register("Anna", "ANNA@student.sdu.dk", "password1")

        assertEquals(AuthResult.Error(AuthError.EMAIL_TAKEN), second)
    }

    @Test
    fun logout_clearsStoredSession() = runTest {
        repository.login("demo@student.sdu.dk", "password123")
        repository.logout()

        assertFalse(repository.isLoggedIn())
        assertNull(storage.saved)
        assertNull(repository.currentUser.value)
    }

    @Test
    fun savedSession_isRestoredOnStart() {
        val saved = FakeSessionStorage(Session("token", "u1", "Demo User", "demo@student.sdu.dk"))
        val restored = AuthRepository(FakeAuthApi(networkDelayMs = 0), saved)

        assertTrue(restored.isLoggedIn())
        assertEquals("Demo User", restored.currentUser.value?.name)
    }

    @Test
    fun updateName_updatesCurrentUserAndStorage() = runTest {
        repository.login("demo@student.sdu.dk", "password123")
        repository.updateName("  New Name ")

        assertEquals("New Name", repository.currentUser.value?.name)
        assertEquals("New Name", storage.saved?.name)
    }

    @Test
    fun changePassword_needsCorrectCurrentPassword() = runTest {
        repository.login("demo@student.sdu.dk", "password123")

        val wrong = repository.changePassword("nope", "newpass123")
        val ok = repository.changePassword("password123", "newpass123")

        assertEquals(AuthResult.Error(AuthError.WRONG_PASSWORD), wrong)
        assertTrue(ok is AuthResult.Success)
    }

    @Test
    fun tokenUnknownToServer_logsOut() = runTest {
        // e.g. session restored from storage after the server forgot it
        val saved = FakeSessionStorage(Session("old", "u1", "Demo User", "demo@student.sdu.dk"))
        val restored = AuthRepository(FakeAuthApi(networkDelayMs = 0), saved)

        val result = restored.updateName("Someone")

        assertEquals(AuthResult.Error(AuthError.SESSION_EXPIRED), result)
        assertFalse(restored.isLoggedIn())
        assertNull(saved.saved)
    }
}

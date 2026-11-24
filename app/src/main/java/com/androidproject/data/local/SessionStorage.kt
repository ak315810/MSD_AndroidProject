package com.androidproject.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.json.JSONObject
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class Session(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
)

interface SessionStorage {
    fun save(session: Session)
    fun load(): Session?
    fun clear()
}

// NFR-S2: the session (token + basic profile) is encrypted with an AES key that lives in the
// Android Keystore, so the key itself never leaves the device. Only the ciphertext ends up in
// SharedPreferences, and that file is excluded from backups (see res/xml/backup_rules.xml).
class KeystoreSessionStorage(context: Context) : SessionStorage {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(session: Session) {
        val json = JSONObject()
            .put("token", session.token)
            .put("userId", session.userId)
            .put("name", session.name)
            .put("email", session.email)
            .toString()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(json.toByteArray(Charsets.UTF_8))

        prefs.edit()
            .putString(KEY_IV, Base64.getEncoder().encodeToString(cipher.iv))
            .putString(KEY_DATA, Base64.getEncoder().encodeToString(encrypted))
            .apply()
    }

    override fun load(): Session? {
        val iv = prefs.getString(KEY_IV, null) ?: return null
        val data = prefs.getString(KEY_DATA, null) ?: return null

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(TAG_LENGTH_BITS, Base64.getDecoder().decode(iv))
            )
            val json = JSONObject(
                String(cipher.doFinal(Base64.getDecoder().decode(data)), Charsets.UTF_8)
            )
            Session(
                token = json.getString("token"),
                userId = json.getString("userId"),
                name = json.getString("name"),
                email = json.getString("email")
            )
        } catch (e: Exception) {
            // Happens if the keystore key was wiped (e.g. lock screen removed). The old data
            // can't be decrypted anymore, so the user simply has to log in again.
            clear()
            null
        }
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let {
            return it.secretKey
        }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "session_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val TAG_LENGTH_BITS = 128
        const val PREFS_NAME = "secure_session"
        const val KEY_IV = "iv"
        const val KEY_DATA = "data"
    }
}

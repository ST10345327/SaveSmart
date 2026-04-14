package com.example.savesmart.util

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object SecurityUtils {

    private const val TAG = "SecurityUtils"
    private const val ALGORITHM = "SHA-256"

    fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance(ALGORITHM)
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { byte -> "%02x".format(byte) }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "SHA-256 algorithm not available: ${e.message}")
            ""
        }
    }

    fun verifyPassword(plainText: String, storedHash: String): Boolean {
        val inputHash = hashPassword(plainText)
        return inputHash == storedHash
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 6) return false
        if (!password.any { it.isLetter() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    fun isValidUsername(username: String): Boolean {
        if (username.length < 3 || username.length > 20) return false
        return username.all { it.isLetterOrDigit() || it == '_' }
    }
}
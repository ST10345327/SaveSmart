package com.savesmart.util

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * SecurityUtils — Cryptographic utility functions for user authentication.
 *
 * References:
 * - Android Developers (2024) Security best practices. Google LLC.
 *   Available at: https://developer.android.com/topic/security/best-practices
 *   (Accessed: 24 March 2026).
 * - Stack Overflow (2021) SHA-256 hashing in Kotlin/Android.
 *   Available at: https://stackoverflow.com/questions/64171624
 *   (Accessed: 24 March 2026).
 * - Java Documentation (2024) MessageDigest. Oracle.
 *   Available at: https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - SHA-256 used for password hashing (R01).
 * - Passwords are NEVER stored in plain text.
 * - Hash is converted to a lowercase hex string for consistent storage.
 * - Salt is not used in this implementation — acceptable for a student POE
 *   but production apps should use bcrypt or Argon2 with salt.
 */
object SecurityUtils {

    private const val TAG = "SecurityUtils"
    private const val ALGORITHM = "SHA-256"

    /**
     * Hashes a plain text password using SHA-256 (R01).
     * Returns a 64-character lowercase hex string.
     *
     * Usage:
     * ```kotlin
     * val hash = SecurityUtils.hashPassword("MyPassword123!")
     * // Stores hash in Room database — never the plain text
     * ```
     *
     * @param password The plain text password entered by the user.
     * @return SHA-256 hash as a 64-character hex string, or empty string on error.
     */
    fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance(ALGORITHM)
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            // Convert byte array to hex string
            hashBytes.joinToString("") { byte -> "%02x".format(byte) }
        } catch (e: NoSuchAlgorithmException) {
            // SHA-256 is guaranteed to be available on all Android devices
            // This exception should never be thrown
            Log.e(TAG, "SHA-256 algorithm not available: ${e.message}")
            ""
        }
    }

    /**
     * Verifies a plain text password against a stored SHA-256 hash (R02).
     *
     * @param plainText The password entered by the user during login.
     * @param storedHash The SHA-256 hash stored in the Room database.
     * @return True if the password matches the stored hash.
     */
    fun verifyPassword(plainText: String, storedHash: String): Boolean {
        val inputHash = hashPassword(plainText)
        return inputHash == storedHash
    }

    /**
     * Validates that a password meets the minimum requirements (R03).
     * Requirements:
     * - Minimum 6 characters
     * - At least one letter
     * - At least one digit
     *
     * @param password The password to validate.
     * @return True if the password meets all requirements.
     */
    fun isPasswordValid(password: String): Boolean {
        if (password.length < 6) return false
        if (!password.any { it.isLetter() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    /**
     * Validates that a username meets the minimum requirements (R03).
     * Requirements:
     * - 3 to 20 characters
     * - Only letters, digits, and underscores
     *
     * @param username The username to validate.
     * @return True if the username is valid.
     */
    fun isUsernameValid(username: String): Boolean {
        if (username.length < 3 || username.length > 20) return false
        return username.all { it.isLetterOrDigit() || it == '_' }
    }
}

/**
 * Reference:
 * - Android Developers (2024) Android Security Best Practices. Google LLC.
 *   Available at: https://developer.android.com/privacy-and-security (Accessed: 24 March 2026).
 * - MessageDigest API (2024) java.security.MessageDigest. Oracle.
 *   Available at: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/MessageDigest.html
 */

package com.example.savesmart.util

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * SecurityUtils — Cryptographic password hashing and validation (Requirement R01).
 *
 * GitHub commit suggestion:
 *   [util] implement SHA-256 password hashing and validation
 *   - hashPassword() uses SHA-256 for irreversible encryption
 *   - verifyPassword() compares plaintext with stored hash
 *   - isValidPassword() enforces 6+ chars with letters + numbers
 *   - isValidUsername() validates 3–20 chars, alphanumeric + underscore
 *   Refs: R01, R03, T05
 */
object SecurityUtils {

    private const val TAG = "SecurityUtils"
    private const val ALGORITHM = "SHA-256"
    private const val MIN_PASSWORD_LENGTH = 6
    private const val MIN_USERNAME_LENGTH = 3
    private const val MAX_USERNAME_LENGTH = 20

    /**
     * Hash a plaintext password using SHA-256 (Requirement R01).
     * @param password raw user input
     * @return hex-encoded SHA-256 hash, or empty string on error
     */
    fun hashPassword(password: String): String {
        Log.d(TAG, "hashPassword() entry")
        return try {
            val digest = MessageDigest.getInstance(ALGORITHM)
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            val result = hashBytes.joinToString("") { byte -> "%02x".format(byte) }
            Log.d(TAG, "hashPassword() successful — hash length: ${result.length}")
            result
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hashPassword() error — SHA-256 unavailable", e)
            ""
        }
    }

    /**
     * Verify a plaintext password against a stored hash (Requirement R02).
     * @param plainText user-entered password
     * @param storedHash database-stored hash
     * @return true if match, false otherwise
     */
    fun verifyPassword(plainText: String, storedHash: String): Boolean {
        Log.d(TAG, "verifyPassword() entry")
        val inputHash = hashPassword(plainText)
        val isMatch = inputHash == storedHash
        if (isMatch) {
            Log.d(TAG, "verifyPassword() success — password verified")
        } else {
            Log.w(TAG, "verifyPassword() attempted password mismatch")
        }
        return isMatch
    }

    /**
     * Validate password strength (Requirement R03).
     * Requires: 6+ chars, at least 1 letter, at least 1 digit.
     * @param password user-entered password
     * @return true if valid, false otherwise
     */
    fun isValidPassword(password: String): Boolean {
        Log.d(TAG, "isValidPassword() entry — password length: ${password.length}")
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        when {
            !hasMinLength -> {
                Log.w(TAG, "isValidPassword() failed — password less than $MIN_PASSWORD_LENGTH chars")
                return false
            }
            !hasLetter -> {
                Log.w(TAG, "isValidPassword() failed — no letter found")
                return false
            }
            !hasDigit -> {
                Log.w(TAG, "isValidPassword() failed — no digit found")
                return false
            }
            else -> {
                Log.d(TAG, "isValidPassword() success")
                return true
            }
        }
    }

    /**
     * Validate username format (Requirement R03).
     * Requires: 3–20 chars, alphanumeric + underscore only.
     * @param username user-entered username
     * @return true if valid, false otherwise
     */
    fun isValidUsername(username: String): Boolean {
        Log.d(TAG, "isValidUsername() entry — username length: ${username.length}")
        val hasValidLength = username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH
        val hasValidChars = username.all { it.isLetterOrDigit() || it == '_' }

        when {
            !hasValidLength -> {
                Log.w(TAG, "isValidUsername() failed — length not in range [$MIN_USERNAME_LENGTH–$MAX_USERNAME_LENGTH]")
                return false
            }
            !hasValidChars -> {
                Log.w(TAG, "isValidUsername() failed — invalid character detected")
                return false
            }
            else -> {
                Log.d(TAG, "isValidUsername() success")
                return true
            }
        }
    }
}
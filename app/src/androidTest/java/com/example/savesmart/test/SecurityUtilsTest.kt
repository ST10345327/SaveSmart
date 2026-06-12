package com.savesmart.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * SecurityUtilsTest — Unit tests for password hashing and validation (T05).
 *
 * References:
 * - Android Developers (2024) Build local unit tests. Google LLC.
 *   Available at: https://developer.android.com/training/testing/local-tests
 *   (Accessed: 24 March 2026).
 */
class SecurityUtilsTest {

    // ── Password hashing ──────────────────────────────────────────────────────

    @Test
    fun `hashPassword returns 64 character hex string`() {
        val hash = SecurityUtils.hashPassword("Password123")
        assertEquals(64, hash.length)
    }

    @Test
    fun `hashPassword produces consistent results for same input`() {
        val hash1 = SecurityUtils.hashPassword("Password123")
        val hash2 = SecurityUtils.hashPassword("Password123")
        assertEquals(hash1, hash2)
    }

    @Test
    fun `hashPassword produces different results for different inputs`() {
        val hash1 = SecurityUtils.hashPassword("Password123")
        val hash2 = SecurityUtils.hashPassword("password123")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hashPassword hash contains only hex characters`() {
        val hash = SecurityUtils.hashPassword("TestPassword1")
        assertTrue(hash.all { it.isDigit() || it in 'a'..'f' })
    }

    // ── Password verification ─────────────────────────────────────────────────

    @Test
    fun `verifyPassword returns true for matching password`() {
        val hash = SecurityUtils.hashPassword("Secure123")
        assertTrue(SecurityUtils.verifyPassword("Secure123", hash))
    }

    @Test
    fun `verifyPassword returns false for wrong password`() {
        val hash = SecurityUtils.hashPassword("Secure123")
        assertFalse(SecurityUtils.verifyPassword("Wrong123", hash))
    }

    @Test
    fun `verifyPassword is case sensitive`() {
        val hash = SecurityUtils.hashPassword("Password123")
        assertFalse(SecurityUtils.verifyPassword("password123", hash))
    }

    // ── Password validation ───────────────────────────────────────────────────

    @Test
    fun `isPasswordValid returns true for valid password`() {
        assertTrue(SecurityUtils.isPasswordValid("Pass123"))
    }

    @Test
    fun `isPasswordValid returns false for too short password`() {
        assertFalse(SecurityUtils.isPasswordValid("Pa1"))
    }

    @Test
    fun `isPasswordValid returns false for no digits`() {
        assertFalse(SecurityUtils.isPasswordValid("Password"))
    }

    @Test
    fun `isPasswordValid returns false for no letters`() {
        assertFalse(SecurityUtils.isPasswordValid("123456"))
    }

    @Test
    fun `isPasswordValid returns true for exactly 6 chars`() {
        assertTrue(SecurityUtils.isPasswordValid("Pass1a"))
    }

    // ── Username validation ───────────────────────────────────────────────────

    @Test
    fun `isUsernameValid returns true for valid username`() {
        assertTrue(SecurityUtils.isUsernameValid("olebogeng"))
    }

    @Test
    fun `isUsernameValid returns true with underscore`() {
        assertTrue(SecurityUtils.isUsernameValid("oleg_123"))
    }

    @Test
    fun `isUsernameValid returns false for too short username`() {
        assertFalse(SecurityUtils.isUsernameValid("ab"))
    }

    @Test
    fun `isUsernameValid returns false for too long username`() {
        assertFalse(SecurityUtils.isUsernameValid("a".repeat(21)))
    }

    @Test
    fun `isUsernameValid returns false for special characters`() {
        assertFalse(SecurityUtils.isUsernameValid("user@name"))
    }

    @Test
    fun `isUsernameValid returns false for spaces`() {
        assertFalse(SecurityUtils.isUsernameValid("user name"))
    }
}

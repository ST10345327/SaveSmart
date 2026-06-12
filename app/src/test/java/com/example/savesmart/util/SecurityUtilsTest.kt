package com.example.savesmart.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Requirement R01: Test security utility functions.
 */
class SecurityUtilsTest {

    @Test
    fun testHashPassword_Consistency() {
        val password = "Password123"
        val hash1 = SecurityUtils.hashPassword(password)
        val hash2 = SecurityUtils.hashPassword(password)
        
        // Same password must result in same hash
        assertEquals(hash1, hash2)
        assertTrue(hash1.isNotEmpty())
    }

    @Test
    fun testHashPassword_Uniqueness() {
        val hash1 = SecurityUtils.hashPassword("Password1")
        val hash2 = SecurityUtils.hashPassword("Password2")
        
        // Different passwords must result in different hashes
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun testIsValidPassword() {
        // Must be >= 6 chars, have letter and digit
        assertTrue(SecurityUtils.isValidPassword("Pass123"))
        assertFalse(SecurityUtils.isValidPassword("short"))      // too short
        assertFalse(SecurityUtils.isValidPassword("nonumber"))   // no digit
        assertFalse(SecurityUtils.isValidPassword("1234567"))    // no letter
    }

    @Test
    fun testIsValidUsername() {
        // 3-20 chars, alphanumeric or underscore
        assertTrue(SecurityUtils.isValidUsername("user_name1"))
        assertFalse(SecurityUtils.isValidUsername("hi"))         // too short
        assertFalse(SecurityUtils.isValidUsername("user@name"))   // invalid char
    }
}

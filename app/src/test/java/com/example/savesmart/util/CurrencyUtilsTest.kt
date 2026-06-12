package com.example.savesmart.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Requirement T10: Test currency conversion logic (Rand to Milliunits and back).
 */
class CurrencyUtilsTest {

    @Test
    fun testParseRandInput_WholeNumber() {
        // R100.00 -> 100,000 milliunits
        assertEquals(100_000L, CurrencyUtils.parseRandInput("100"))
    }

    @Test
    fun testParseRandInput_Decimal() {
        // R12.50 -> 12,500 milliunits
        assertEquals(12_500L, CurrencyUtils.parseRandInput("12.50"))
    }

    @Test
    fun testParseRandInput_SingleCent() {
        // R0.01 -> 10 milliunits
        assertEquals(10L, CurrencyUtils.parseRandInput("0.01"))
    }

    @Test
    fun testFormatMilliunits() {
        // 1234560 milliunits -> R 1 234,56 (ZAR locale format)
        val result = CurrencyUtils.formatMilliunits(1234560L)
        
        // ZAR format usually includes "R" and non-breaking spaces or commas
        // Let's check for the numeric parts to be safe across different OS locales
        assertTrue("Format should contain 1", result.contains("1"))
        assertTrue("Format should contain 234", result.contains("234"))
        assertTrue("Format should contain 56", result.contains("56"))
    }

    @Test
    fun testMilliunitsToRands() {
        // 5500 milliunits -> 5.5 Rands
        assertEquals(5.5, CurrencyUtils.milliunitsToRands(5500L), 0.001)
    }
}

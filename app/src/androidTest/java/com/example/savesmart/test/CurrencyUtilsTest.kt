package com.savesmart.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * CurrencyUtilsTest — Unit tests for milliunit currency conversion (T05).
 *
 * References:
 * - Android Developers (2024) Build local unit tests. Google LLC.
 *   Available at: https://developer.android.com/training/testing/local-tests
 *   (Accessed: 24 March 2026).
 * - JUnit 4 (2024) JUnit 4 documentation.
 *   Available at: https://junit.org/junit4/
 *   (Accessed: 24 March 2026).
 *
 * Tests cover:
 * - Rand to milliunit conversion (T10)
 * - Milliunit to Rand conversion (T10)
 * - Display string formatting
 * - User input parsing
 * - Budget status calculation (R15, R16)
 * - Progress fraction calculation (R15)
 */
class CurrencyUtilsTest {

    // ── Rand to milliunit conversion ──────────────────────────────────────────

    @Test
    fun `randsToMilliunits converts R1 correctly`() {
        assertEquals(1_000L, CurrencyUtils.randsToMilliunits(1.0))
    }

    @Test
    fun `randsToMilliunits converts R12_50 correctly`() {
        assertEquals(12_500L, CurrencyUtils.randsToMilliunits(12.50))
    }

    @Test
    fun `randsToMilliunits converts R1000 correctly`() {
        assertEquals(1_000_000L, CurrencyUtils.randsToMilliunits(1000.0))
    }

    @Test
    fun `randsToMilliunits converts R0 correctly`() {
        assertEquals(0L, CurrencyUtils.randsToMilliunits(0.0))
    }

    @Test
    fun `randsToMilliunits converts R0_01 correctly`() {
        assertEquals(10L, CurrencyUtils.randsToMilliunits(0.01))
    }

    // ── Milliunit to Rand conversion ──────────────────────────────────────────

    @Test
    fun `milliunitsToRands converts 1000 correctly`() {
        assertEquals(1.0, CurrencyUtils.milliunitsToRands(1_000L), 0.001)
    }

    @Test
    fun `milliunitsToRands converts 12500 correctly`() {
        assertEquals(12.50, CurrencyUtils.milliunitsToRands(12_500L), 0.001)
    }

    @Test
    fun `milliunitsToRands converts 0 correctly`() {
        assertEquals(0.0, CurrencyUtils.milliunitsToRands(0L), 0.001)
    }

    // ── Display string formatting ─────────────────────────────────────────────

    @Test
    fun `formatMilliunits formats R12_50 correctly`() {
        assertEquals("R12.50", CurrencyUtils.formatMilliunits(12_500L))
    }

    @Test
    fun `formatMilliunits formats R0 correctly`() {
        assertEquals("R0.00", CurrencyUtils.formatMilliunits(0L))
    }

    @Test
    fun `formatMilliunits formats R1000 correctly`() {
        assertEquals("R1000.00", CurrencyUtils.formatMilliunits(1_000_000L))
    }

    // ── User input parsing ────────────────────────────────────────────────────

    @Test
    fun `parseRandInput parses plain number`() {
        assertEquals(12_500L, CurrencyUtils.parseRandInput("12.50"))
    }

    @Test
    fun `parseRandInput parses with R prefix`() {
        assertEquals(12_500L, CurrencyUtils.parseRandInput("R12.50"))
    }

    @Test
    fun `parseRandInput parses comma decimal separator`() {
        assertEquals(12_500L, CurrencyUtils.parseRandInput("12,50"))
    }

    @Test
    fun `parseRandInput returns null for empty string`() {
        assertNull(CurrencyUtils.parseRandInput(""))
    }

    @Test
    fun `parseRandInput returns null for invalid input`() {
        assertNull(CurrencyUtils.parseRandInput("abc"))
    }

    @Test
    fun `parseRandInput handles spaces`() {
        assertEquals(1_000_000L, CurrencyUtils.parseRandInput("1 000.00"))
    }

    // ── Budget status calculation ─────────────────────────────────────────────

    @Test
    fun `getBudgetStatus returns GOOD when well within budget`() {
        val status = CurrencyUtils.getBudgetStatus(
            spentMilliunits    = 200_000L,  // R200 spent
            maxGoalMilliunits  = 500_000L   // R500 goal
        )
        assertEquals(BudgetStatus.GOOD, status)
    }

    @Test
    fun `getBudgetStatus returns CLOSE when over 80 percent`() {
        val status = CurrencyUtils.getBudgetStatus(
            spentMilliunits    = 430_000L,  // R430 spent (86%)
            maxGoalMilliunits  = 500_000L   // R500 goal
        )
        assertEquals(BudgetStatus.CLOSE, status)
    }

    @Test
    fun `getBudgetStatus returns OVER when exceeding max goal`() {
        val status = CurrencyUtils.getBudgetStatus(
            spentMilliunits    = 600_000L,  // R600 spent
            maxGoalMilliunits  = 500_000L   // R500 goal
        )
        assertEquals(BudgetStatus.OVER, status)
    }

    @Test
    fun `getBudgetStatus returns NO_GOAL when maxGoal is null`() {
        val status = CurrencyUtils.getBudgetStatus(
            spentMilliunits    = 200_000L,
            maxGoalMilliunits  = null
        )
        assertEquals(BudgetStatus.NO_GOAL, status)
    }

    @Test
    fun `getBudgetStatus returns OVER when exactly at limit`() {
        val status = CurrencyUtils.getBudgetStatus(
            spentMilliunits    = 500_000L,  // Exactly R500 = R500 limit
            maxGoalMilliunits  = 500_000L
        )
        // Exactly at limit is NOT over — should be CLOSE
        assertEquals(BudgetStatus.CLOSE, status)
    }

    // ── Progress fraction calculation ─────────────────────────────────────────

    @Test
    fun `getProgressFraction returns 0_5 at half spend`() {
        val progress = CurrencyUtils.getProgressFraction(250_000L, 500_000L)
        assertEquals(0.5f, progress, 0.001f)
    }

    @Test
    fun `getProgressFraction caps at 1_0 when over budget`() {
        val progress = CurrencyUtils.getProgressFraction(800_000L, 500_000L)
        assertEquals(1.0f, progress, 0.001f)
    }

    @Test
    fun `getProgressFraction returns 0 when no spending`() {
        val progress = CurrencyUtils.getProgressFraction(0L, 500_000L)
        assertEquals(0.0f, progress, 0.001f)
    }

    @Test
    fun `getProgressFraction returns 0 when goal is zero`() {
        val progress = CurrencyUtils.getProgressFraction(100_000L, 0L)
        assertEquals(0.0f, progress, 0.001f)
    }
}

package com.savesmart.util

import android.util.Log

/**
 * CurrencyUtils — Utility functions for milliunit currency conversion.
 *
 * References:
 * - YNAB API (2024) YNAB API v1 documentation: milliunits and data model.
 *   You Need A Budget LLC. Available at: https://api.ynab.com/v1
 *   (Accessed: 24 March 2026).
 * - Stack Overflow (2019) Avoid floating point errors with money in Kotlin.
 *   Available at: https://stackoverflow.com/questions/3730019
 *   (Accessed: 24 March 2026).
 *
 * Milliunit convention (T10, adopted from YNAB API):
 * - All monetary amounts stored as Long integers (milliunits)
 * - 1 000 milliunits = R1.00
 * - R12.50 is stored as 12_500L
 * - R1 000.00 is stored as 1_000_000L
 *
 * This prevents floating point rounding errors in budget calculations.
 * For example: 0.1 + 0.2 = 0.30000000000000004 in Double arithmetic.
 * With milliunits: 100 + 200 = 300 exactly.
 */
object CurrencyUtils {

    private const val TAG = "CurrencyUtils"

    /** Conversion factor: 1 Rand = 1 000 milliunits. */
    const val MILLIUNITS_PER_RAND = 1_000L

    // ── Conversion functions ──────────────────────────────────────────────────

    /**
     * Converts a Rand amount (Double) to milliunits (Long).
     * e.g. 12.50 → 12_500L
     *
     * @param rands The amount in Rands.
     * @return Amount in milliunits as Long.
     */
    fun randsToMilliunits(rands: Double): Long {
        return (rands * MILLIUNITS_PER_RAND).toLong()
    }

    /**
     * Converts milliunits (Long) to Rands (Double).
     * e.g. 12_500L → 12.50
     *
     * @param milliunits The amount in milliunits.
     * @return Amount in Rands as Double.
     */
    fun milliunitsToRands(milliunits: Long): Double {
        return milliunits.toDouble() / MILLIUNITS_PER_RAND
    }

    /**
     * Formats milliunits as a display string with the Rand symbol.
     * e.g. 12_500L → "R12.50"
     * e.g. 1_000_000L → "R1 000.00"
     *
     * @param milliunits The amount in milliunits.
     * @return Formatted Rand string e.g. "R12.50".
     */
    fun formatMilliunits(milliunits: Long): String {
        val rands = milliunitsToRands(milliunits)
        return "R%.2f".format(rands)
    }

    /**
     * Parses a user-entered Rand string to milliunits.
     * Handles input formats: "12.50", "12,50", "R12.50", "1 000.00"
     * Returns null if the input cannot be parsed.
     *
     * @param input The raw string entered by the user.
     * @return Amount in milliunits, or null if parsing fails.
     */
    fun parseRandInput(input: String): Long? {
        return try {
            // Remove currency symbol, spaces, and normalise decimal separator
            val cleaned = input
                .replace("R", "")
                .replace(" ", "")
                .replace(",", ".")
                .trim()
            val rands = cleaned.toDouble()
            randsToMilliunits(rands)
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse Rand input: $input — ${e.message}")
            null
        }
    }

    /**
     * Calculates the budget status for a category based on spending vs goals.
     * Returns a BudgetStatus enum used to colour the category card (R15, R16).
     *
     * Status rules:
     * - OVER: spending > maxGoal
     * - CLOSE: spending > 80% of maxGoal
     * - GOOD: spending <= 80% of maxGoal
     * - NO_GOAL: no maxGoal set
     *
     * @param spentMilliunits Current spending in milliunits.
     * @param maxGoalMilliunits Maximum goal in milliunits (nullable).
     * @return The BudgetStatus for this category.
     */
    fun getBudgetStatus(spentMilliunits: Long, maxGoalMilliunits: Long?): BudgetStatus {
        if (maxGoalMilliunits == null || maxGoalMilliunits <= 0L) {
            return BudgetStatus.NO_GOAL
        }
        return when {
            spentMilliunits > maxGoalMilliunits -> BudgetStatus.OVER
            spentMilliunits > (maxGoalMilliunits * 0.8).toLong() -> BudgetStatus.CLOSE
            else -> BudgetStatus.GOOD
        }
    }

    /**
     * Calculates progress as a 0.0–1.0 Float for ProgressBar rendering (R15).
     * Capped at 1.0 (100%) — overspending shown as full red bar.
     *
     * @param spentMilliunits Current spending in milliunits.
     * @param maxGoalMilliunits Maximum goal in milliunits.
     * @return Progress fraction between 0.0 and 1.0.
     */
    fun getProgressFraction(spentMilliunits: Long, maxGoalMilliunits: Long): Float {
        if (maxGoalMilliunits <= 0L) return 0f
        return (spentMilliunits.toFloat() / maxGoalMilliunits.toFloat()).coerceIn(0f, 1f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * BudgetStatus — Represents the spending status of a category (R15, R16).
 * Used to determine the colour of category progress bars on the dashboard.
 *
 * Colour mapping (from design system):
 * - GOOD  → Green  (#16A34A) — within budget
 * - CLOSE → Amber  (#F59E0B) — approaching limit (>80%)
 * - OVER  → Red    (#DC2626) — limit exceeded
 * - NO_GOAL → Blue (#1A6FE8) — no goal set, neutral display
 */
enum class BudgetStatus {
    /** Spending is within budget — show green progress bar. */
    GOOD,
    /** Spending exceeds 80% of max goal — show amber progress bar. */
    CLOSE,
    /** Spending exceeds max goal — show red progress bar with Over! badge. */
    OVER,
    /** No goal set for this category — show neutral blue. */
    NO_GOAL
}

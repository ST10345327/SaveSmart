/**
 * Reference:
 * - Android Developers (2024) NumberFormat. Google LLC.
 *   Available at: https://developer.android.com/reference/java/text/NumberFormat (Accessed: 24 March 2026).
 */

package com.example.savesmart.util

import android.util.Log
import java.text.NumberFormat
import java.util.Locale

/**
 * Utility for handling currency conversions and formatting (Requirement T10).
 * Uses NumberFormat for professional currency presentation as required.
 */
object CurrencyUtils {

    private const val TAG = "CurrencyUtils"
    const val MILLIUNITS_PER_RAND = 1_000L

    fun randsToMilliunits(rands: Double): Long {
        return (rands * MILLIUNITS_PER_RAND).toLong()
    }

    fun milliunitsToRands(milliunits: Long): Double {
        return milliunits.toDouble() / MILLIUNITS_PER_RAND
    }

    /**
     * Requirement: Use NumberFormat in the app.
     */
    fun formatMilliunits(milliunits: Long): String {
        val rands = milliunitsToRands(milliunits)
        val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        return format.format(rands)
    }

    fun parseRandInput(input: String): Long? {
        return try {
            // Security Audit: Check for excessive length to prevent memory issues
            if (input.length > 15) return null

            val cleaned = input
                .replace(Regex("[^0-9.,]"), "")
                .replace(",", ".")
                .trim()
            
            if (cleaned.isEmpty()) return null

            val rands = cleaned.toDouble()
            
            // Limit to reasonable financial bounds (max R100 million)
            if (rands < 0 || rands > 100_000_000) return null

            randsToMilliunits(rands)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse Rand input: $input — ${e.message}")
            null
        }
    }

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

    fun getProgressFraction(spentMilliunits: Long, maxGoalMilliunits: Long): Float {
        if (maxGoalMilliunits <= 0L) return 0f
        return (spentMilliunits.toFloat() / maxGoalMilliunits.toFloat()).coerceIn(0f, 1f)
    }
}

enum class BudgetStatus {
    GOOD,
    CLOSE,
    OVER,
    NO_GOAL
}

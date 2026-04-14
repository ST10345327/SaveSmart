package com.example.savesmart.util

import android.util.Log

object CurrencyUtils {

    private const val TAG = "CurrencyUtils"
    const val MILLIUNITS_PER_RAND = 1_000L

    fun randsToMilliunits(rands: Double): Long {
        return (rands * MILLIUNITS_PER_RAND).toLong()
    }

    fun milliunitsToRands(milliunits: Long): Double {
        return milliunits.toDouble() / MILLIUNITS_PER_RAND
    }

    fun formatMilliunits(milliunits: Long): String {
        val rands = milliunitsToRands(milliunits)
        return "R%.2f".format(rands)
    }

    fun parseRandInput(input: String): Long? {
        return try {
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
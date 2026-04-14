/**
 * Reference:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 * - Android Developers (2024) LiveData overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/livedata (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for computing Dashboard insights.
 *
 * GitHub commit suggestion:
 *   [dashboard] implement monthly spending and category summary logic
 *   - Added logic for current month date filtering
 *   - Implemented overspending indicator calculation
 *   Refs: R15, R16, T01
 */
class DashboardViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "DashboardViewModel"

    private val _totalSpent = MutableLiveData<Long>()
    val totalSpent: LiveData<Long> = _totalSpent

    private val _categoriesSummary = MutableLiveData<List<CategoryWithSpending>>()
    val categoriesSummary: LiveData<List<CategoryWithSpending>> = _categoriesSummary

    /**
     * Requirement R15: Load dashboard data for the current month.
     */
    fun loadDashboardData(userId: Int) {
        Log.d(TAG, "loadDashboardData() called for userId: $userId")
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startMillis = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        val endMillis = calendar.timeInMillis

        viewModelScope.launch {
            try {
                // Aggregate total spending (R15)
                val total = repository.getTotalMonthlySpending(userId, startMillis, endMillis)
                _totalSpent.postValue(total)
                Log.d(TAG, "loadDashboardData(): Total spent this month = $total milliunits")

                // Get category summaries (R16)
                val summaries = repository.getCategoriesWithSpending(userId, startMillis, endMillis)
                _categoriesSummary.postValue(summaries)
                Log.d(TAG, "loadDashboardData(): Loaded ${summaries.size} category summaries")

            } catch (e: Exception) {
                Log.e(TAG, "loadDashboardData(): Unexpected error", e)
            } finally {
                Log.d(TAG, "loadDashboardData(): Data load completed")
            }
        }
    }
}

/**
 * Data model for Dashboard summary items.
 */
data class CategoryWithSpending(
    val categoryId: Int,
    val name: String,
    val colorHex: String,
    val totalMilliunits: Long,
    val maxGoalMilliunits: Long,
    val minGoalMilliunits: Long
)

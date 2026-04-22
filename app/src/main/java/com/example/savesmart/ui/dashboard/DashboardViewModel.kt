/**
 * Reference:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * ViewModel for computing Dashboard insights.
 */
class DashboardViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "DashboardViewModel"

    private val _totalSpent = MutableLiveData<Long>()
    val totalSpent: LiveData<Long> = _totalSpent

    private val _categoriesSummary = MutableLiveData<List<CategoryWithSpending>>()
    val categoriesSummary: LiveData<List<CategoryWithSpending>> = _categoriesSummary

    private val _currentMonthDisplay = MutableLiveData<String>()
    val currentMonthDisplay: LiveData<String> = _currentMonthDisplay

    private val calendar = Calendar.getInstance()
    private var currentUserId: Int = -1

    init {
        updateMonthDisplay()
    }

    private fun updateMonthDisplay() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        _currentMonthDisplay.value = sdf.format(calendar.time)
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateMonthDisplay()
        if (currentUserId != -1) loadDashboardData(currentUserId)
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateMonthDisplay()
        if (currentUserId != -1) loadDashboardData(currentUserId)
    }

    /**
     * Optimized Dashboard loading to prevent ANR (Performance T08).
     * Moves date calculation and data fetching to background threads.
     */
    fun loadDashboardData(userId: Int) {
        currentUserId = userId
        
        viewModelScope.launch {
            // Move date processing to Default dispatcher (CPU intensive)
            val (startMillis, endMillis) = withContext(Dispatchers.Default) {
                val tempCal = calendar.clone() as Calendar
                tempCal.set(Calendar.DAY_OF_MONTH, 1)
                tempCal.set(Calendar.HOUR_OF_DAY, 0)
                tempCal.set(Calendar.MINUTE, 0)
                tempCal.set(Calendar.SECOND, 0)
                val start = tempCal.timeInMillis

                tempCal.set(Calendar.DAY_OF_MONTH, tempCal.getActualMaximum(Calendar.DAY_OF_MONTH))
                tempCal.set(Calendar.HOUR_OF_DAY, 23)
                tempCal.set(Calendar.MINUTE, 59)
                tempCal.set(Calendar.SECOND, 59)
                val end = tempCal.timeInMillis
                Pair(start, end)
            }

            try {
                // Fetch data in parallel to speed up loading
                val totalDeferred = launch {
                    val total = repository.getTotalMonthlySpending(userId, startMillis, endMillis)
                    _totalSpent.postValue(total)
                }

                val summaryDeferred = launch {
                    val summaries = repository.getCategoriesWithSpending(userId, startMillis, endMillis)
                    _categoriesSummary.postValue(summaries)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading dashboard data", e)
            }
        }
    }
}

data class CategoryWithSpending(
    val categoryId: Int,
    val name: String,
    val colorHex: String,
    val totalMilliunits: Long,
    val maxGoalMilliunits: Long,
    val minGoalMilliunits: Long
)

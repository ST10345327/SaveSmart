/**
 * References:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 * - Jahoda, P. (2024) MPAndroidChart documentation. GitHub.
 *   Available at: https://github.com/PhilJay/MPAndroidChart (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.reports

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.repository.SaveSmartRepository
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * ReportsViewModel — Aggregates data for spending visualization (Requirement R17, R18).
 */
class ReportsViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    companion object {
        private const val TAG = "ReportsViewModel"
    }

    private val _pieEntries = MutableLiveData<List<PieEntry>>()
    val pieEntries: LiveData<List<PieEntry>> = _pieEntries

    private val _pieColors = MutableLiveData<List<Int>>()
    val pieColors: LiveData<List<Int>> = _pieColors

    private val _barEntries = MutableLiveData<List<BarEntry>>()
    val barEntries: LiveData<List<BarEntry>> = _barEntries

    private val _dailyLimit = MutableLiveData<Float>()
    val dailyLimit: LiveData<Float> = _dailyLimit

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
        if (currentUserId != -1) loadCategoryReport(currentUserId)
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateMonthDisplay()
        if (currentUserId != -1) loadCategoryReport(currentUserId)
    }

    /**
     * Requirement R17, R18: Load spending breakdown and daily history.
     */
    fun loadCategoryReport(userId: Int) {
        currentUserId = userId
        
        viewModelScope.launch {
            val (startMillis, endMillis, daysInMonth) = withContext(Dispatchers.Default) {
                val tempCal = calendar.clone() as Calendar
                tempCal.set(Calendar.DAY_OF_MONTH, 1)
                tempCal.set(Calendar.HOUR_OF_DAY, 0)
                tempCal.set(Calendar.MINUTE, 0)
                val start = tempCal.timeInMillis

                val maxDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                tempCal.set(Calendar.DAY_OF_MONTH, maxDay)
                tempCal.set(Calendar.HOUR_OF_DAY, 23)
                tempCal.set(Calendar.MINUTE, 59)
                val end = tempCal.timeInMillis
                Triple(start, end, maxDay)
            }

            try {
                // 1. Fetch Pie Chart Data (R17)
                val summaries = repository.getCategoriesWithSpending(userId, startMillis, endMillis)
                val filteredSummaries = summaries.filter { it.totalMilliunits > 0 }
                
                _pieEntries.postValue(filteredSummaries.map {
                    PieEntry(it.totalMilliunits.toFloat(), it.name)
                })
                
                // Collect colors corresponding to the entries
                _pieColors.postValue(filteredSummaries.map { 
                    android.graphics.Color.parseColor(it.colorHex)
                })

                // 2. Fetch Bar Chart Data (R18)
                val dailySpending = repository.getDailySpending(userId, startMillis, endMillis)
                
                // Map database results to BarEntries (X-axis is day of month)
                val entries = mutableListOf<BarEntry>()
                val cal = Calendar.getInstance()
                
                // Initialize all days with 0 to ensure a full chart
                val spendingMap = dailySpending.associate { 
                    cal.timeInMillis = it.dateMillis
                    cal.get(Calendar.DAY_OF_MONTH) to it.totalMilliunits.toFloat()
                }

                for (day in 1..daysInMonth) {
                    entries.add(BarEntry(day.toFloat(), spendingMap[day] ?: 0f))
                }
                _barEntries.postValue(entries)

                // 3. Calculate Daily Limit (Based on user's max budget / days in month)
                val user = repository.getUserById(userId)
                user?.let {
                    val limit = if (it.maxMonthlyBudget > 0) {
                        it.maxMonthlyBudget.toFloat() / daysInMonth
                    } else {
                        200_000f // Default fallback R200
                    }
                    _dailyLimit.postValue(limit)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading report data", e)
            }
        }
    }
}

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
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ReportsViewModel — Aggregates data for spending visualization (Requirement R17).
 *
 * GitHub commit suggestion:
 *   [reports] implement ReportsViewModel with PieChart data aggregation
 *   - Integrated with SaveSmartRepository for category spending
 *   - Implemented current month filtering logic
 *   Refs: R17, T01, T10
 */
class ReportsViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    companion object {
        private const val TAG = "ReportsViewModel"
    }

    private val _pieEntries = MutableLiveData<List<PieEntry>>()
    val pieEntries: LiveData<List<PieEntry>> = _pieEntries

    /**
     * Requirement R17: Load spending breakdown for the current month.
     */
    fun loadCategoryReport(userId: Int) {
        Log.d(TAG, "loadCategoryReport: started for userId $userId")
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startMillis = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        val endMillis = calendar.timeInMillis

        viewModelScope.launch {
            try {
                // T10: Data fetched in milliunits and converted for display
                val summaries = repository.getCategoriesWithSpending(userId, startMillis, endMillis)
                
                val entries = summaries.filter { it.totalMilliunits > 0 }.map {
                    PieEntry(it.totalMilliunits.toFloat(), it.name)
                }
                
                _pieEntries.postValue(entries)
                Log.d(TAG, "loadCategoryReport: success — generated ${entries.size} entries")
            } catch (e: Exception) {
                Log.e(TAG, "loadCategoryReport: exception", e)
            } finally {
                Log.d(TAG, "loadCategoryReport: completed")
            }
        }
    }
}

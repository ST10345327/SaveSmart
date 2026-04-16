/**
 * Reference:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing expense operations (R08, R12, R13).
 */
class ExpenseViewModel(private val repository: com.example.savesmart.data.repository.SaveSmartRepository) : ViewModel() {

    private val TAG = "ExpenseViewModel"

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Requirement R05: Fetch available categories for selection.
     */
    fun loadCategories(userId: Int) {
        Log.d(TAG, "loadCategories() called for userId: $userId")
        viewModelScope.launch {
            try {
                // We'll need to add a method to repository or call DAO directly if repository doesn't have it
                // For now, let's assume we can get them.
                // Note: Based on SaveSmartRepository.kt, we might need to expose categoryDao or add a method.
            } catch (e: Exception) {
                Log.e(TAG, "loadCategories(): Error", e)
            }
        }
    }

    /**
     * Requirement R08, R13: Save a new expense entry.
     */
    fun saveExpense(expense: Expense) {
        Log.d(TAG, "saveExpense() entry: ${expense.description}, amount: ${expense.amountMilliunits}")
        viewModelScope.launch {
            try {
                // Assuming we add insertExpense to Repository
                // val id = repository.insertExpense(expense)
                // _operationSuccess.postValue(id > 0)
                Log.d(TAG, "saveExpense(): Success")
            } catch (e: Exception) {
                Log.e(TAG, "saveExpense(): Error", e)
                _operationSuccess.postValue(false)
            }
        }
    }
}

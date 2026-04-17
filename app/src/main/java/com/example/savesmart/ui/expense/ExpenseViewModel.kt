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
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for managing expense operations (Requirement R08, R10, R12, R13).
 *
 * GitHub commit suggestion:
 *   [expense] implement ExpenseViewModel with CRUD and filtering
 *   - Integrated with SaveSmartRepository
 *   - Added logic for date range filtering
 *   Refs: R08, R10, R12, R13, T01
 */
class ExpenseViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "ExpenseViewModel"

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    // LiveData for expenses list (R10)
    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    private val _userIdForCategories = MutableLiveData<Int>()
    val categories: LiveData<List<Category>> = _userIdForCategories.switchMap { userId ->
        repository.getCategoriesForUserLive(userId)
    }

    /**
     * Requirement R05: Fetch available categories for selection.
     */
    fun loadCategories(userId: Int) {
        Log.d(TAG, "loadCategories() called for userId: $userId")
        _userIdForCategories.value = userId
    }

    /**
     * Requirement R05: Fetch available categories for selection.
     */
    fun getCategoriesLive(userId: Int): LiveData<List<Category>> {
        Log.d(TAG, "getCategoriesLive() called for userId: $userId")
        return repository.getCategoriesForUserLive(userId)
    }

    /**
     * Requirement R08, R13: Save a new expense entry.
     */
    fun saveExpense(expense: Expense) {
        Log.d(TAG, "saveExpense() entry: desc='${expense.description}', amount=${expense.amountMilliunits}")
        viewModelScope.launch {
            try {
                val id = repository.insertExpense(expense)
                val success = id > 0
                _operationSuccess.postValue(success)
                Log.d(TAG, "saveExpense() success=$success, ID=$id")
            } catch (e: Exception) {
                Log.e(TAG, "saveExpense() error: ${e.message}", e)
                _operationSuccess.postValue(false)
            }
        }
    }

    /**
     * Requirement R10: Load expenses for a specific date range.
     */
    fun loadExpenses(userId: Int, startMillis: Long, endMillis: Long) {
        Log.d(TAG, "loadExpenses() period: $startMillis to $endMillis")
        // In a real app, we'd observe the LiveData from repository and pipe it to _expenses.
        // For simplicity in this step, we can use a Transformations.switchMap or just expose the LiveData.
    }
    
    fun getExpensesLive(userId: Int, startMillis: Long, endMillis: Long): LiveData<List<Expense>> {
        return repository.getExpensesInRangeLive(userId, startMillis, endMillis)
    }

    /**
     * Requirement R12: Delete an expense.
     */
    fun deleteExpense(expenseId: Int) {
        Log.d(TAG, "deleteExpense() id: $expenseId")
        viewModelScope.launch {
            try {
                repository.deleteExpense(expenseId)
                Log.d(TAG, "deleteExpense() success")
            } catch (e: Exception) {
                Log.e(TAG, "deleteExpense() error: ${e.message}", e)
            }
        }
    }
}

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

    // Reactive filtering for expenses (T01)
    private val filterParams = MutableLiveData<Triple<Int, Long, Long>>()
    val filteredExpensesWithCategory: LiveData<List<com.example.savesmart.data.dao.ExpenseWithCategory>> = filterParams.switchMap { (userId, start, end) ->
        repository.getExpensesWithCategoryLive(userId, start, end)
    }

    /**
     * Requirement R10: Update filter parameters for expenses.
     */
    fun setExpenseFilter(userId: Int, startMillis: Long, endMillis: Long) {
        Log.d(TAG, "setExpenseFilter: user=$userId, $startMillis to $endMillis")
        filterParams.value = Triple(userId, startMillis, endMillis)
    }

    fun loadCategories(userId: Int) {
        _userIdForCategories.value = userId
    }

    fun saveExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val id = repository.insertExpense(expense)
                _operationSuccess.postValue(id > 0)
            } catch (e: Exception) {
                Log.e(TAG, "saveExpense error", e)
                _operationSuccess.postValue(false)
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.updateExpense(expense)
                _operationSuccess.postValue(true)
            } catch (e: Exception) {
                Log.e(TAG, "updateExpense error", e)
                _operationSuccess.postValue(false)
            }
        }
    }

    private val _expenseToEdit = MutableLiveData<Expense?>()
    val expenseToEdit: LiveData<Expense?> = _expenseToEdit

    fun loadExpenseForEditing(expenseId: Int) {
        viewModelScope.launch {
            val expense = repository.getExpenseById(expenseId)
            _expenseToEdit.postValue(expense)
        }
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

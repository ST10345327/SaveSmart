/**
 * References:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

/**
 * CategoriesViewModel — Manages category-related data and logic (Requirement R05, R06, R07).
 */
class CategoriesViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    companion object {
        private const val TAG = "CategoriesViewModel"
    }

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Requirement R05: Observe categories for a specific user.
     */
    fun getCategories(userId: Int): LiveData<List<Category>> {
        Log.d(TAG, "getCategories: Fetching categories for userId $userId")
        return repository.getCategoriesForUserLive(userId)
    }

    /**
     * Requirement R05, R06: Save or update a category.
     */
    fun saveCategory(category: Category) {
        viewModelScope.launch {
            try {
                if (category.categoryId == 0) {
                    val id = repository.insertCategory(category)
                    _operationSuccess.postValue(id > 0)
                } else {
                    repository.updateCategory(category)
                    _operationSuccess.postValue(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveCategory error", e)
                _operationSuccess.postValue(false)
            }
        }
    }

    /**
     * Requirement R07: Soft delete a category.
     */
    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
        }
    }
}

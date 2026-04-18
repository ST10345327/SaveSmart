/**
 * References:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

/**
 * CategoriesViewModel — Manages category-related data and logic (Requirement R05, R06, R07).
 *
 * GitHub commit suggestion:
 *   [categories] implement CategoriesViewModel with CRUD and soft-delete (R05, R06, R07)
 *   - Integrated with SaveSmartRepository for category operations
 *   - Implemented soft-delete logic (R07)
 *   Refs: R05, R06, R07, T01
 */
class CategoriesViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    companion object {
        private const val TAG = "CategoriesViewModel"
    }

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
        Log.d(TAG, "saveCategory: saving category '${category.name}'")
        viewModelScope.launch {
            try {
                if (category.categoryId == 0) {
                    repository.insertCategory(category)
                } else {
                    repository.updateCategory(category)
                }
                Log.d(TAG, "saveCategory: success")
            } catch (e: Exception) {
                Log.e(TAG, "saveCategory: error", e)
            }
        }
    }

    /**
     * Requirement R07: Soft delete a category.
     */
    fun deleteCategory(categoryId: Int) {
        Log.d(TAG, "deleteCategory: Soft deleting categoryId $categoryId")
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId)
                Log.d(TAG, "deleteCategory: Success")
            } catch (e: Exception) {
                Log.e(TAG, "deleteCategory: Error", e)
            }
        }
    }
}

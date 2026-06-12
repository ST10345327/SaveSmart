/**
 * References:
 * - Android Developers (2024) RecyclerView. Google LLC.
 *   Available at: https://developer.android.com/guide/topics/ui/layout/recyclerview (Accessed: 24 March 2026).
 * - Android Developers (2024) ListAdapter. Google LLC.
 *   Available at: https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.categories

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.data.entity.Category
import com.example.savesmart.databinding.ItemCategoryBinding
import com.example.savesmart.util.CurrencyUtils

/**
 * CategoryManagementAdapter — Adapter for managing the categories list (Requirement R05, R06).
 *
 * GitHub commit suggestion:
 *   [categories] implement CategoryManagementAdapter with goals display
 *   - Displays name, color, and min/max goals (R14)
 *   - Implements callback for edit actions
 *   Refs: R05, R06, R14, T01
 */
class CategoryManagementAdapter(
    private val onEditClick: (Category) -> Unit
) : ListAdapter<Category, CategoryManagementAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    companion object {
        private const val TAG = "CategoryMgmtAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        Log.d(TAG, "onCreateViewHolder: started")
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(category: Category) {
            Log.d(TAG, "bind: binding category '${category.name}'")
            binding.tvCategoryName.text = category.name
            
            try {
                binding.viewColorIndicator.background.setTint(Color.parseColor(category.colorHex))
            } catch (e: Exception) {
                Log.e(TAG, "bind: invalid color hex '${category.colorHex}'", e)
            }

            // Display Goals (Requirement R14)
            val minStr = CurrencyUtils.formatMilliunits(category.minGoalMilliunits ?: 0L)
            val maxStr = CurrencyUtils.formatMilliunits(category.maxGoalMilliunits ?: 0L)
            binding.tvGoalsSummary.text = "Budget: $minStr - $maxStr"

            binding.btnEdit.setOnClickListener {
                Log.d(TAG, "bind: edit clicked for '${category.name}'")
                onEditClick(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.categoryId == newItem.categoryId
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}

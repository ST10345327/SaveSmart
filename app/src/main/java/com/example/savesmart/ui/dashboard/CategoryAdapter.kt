/**
 * Reference:
 * - Android Developers (2024) Create dynamic lists with RecyclerView. Google LLC.
 *   Available at: https://developer.android.com/develop/ui/views/layout/recyclerview (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.databinding.ItemCategorySpendingBinding
import com.example.savesmart.util.CurrencyUtils

/**
 * RecyclerView adapter for displaying category spending summaries on Dashboard.
 *
 * GitHub commit suggestion:
 *   [dashboard] implement CategoryAdapter with budget status indicators
 *   - Added progress bars and color-coded status indicators
 *   - Implemented overspending highlighting (R16)
 *   Refs: R15, R16, T06
 */
class CategoryAdapter : ListAdapter<CategoryWithSpending, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private val TAG = "CategoryAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategorySpendingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    class CategoryViewHolder(private val binding: ItemCategorySpendingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryWithSpending) {
            Log.d("CategoryViewHolder", "Binding category: ${category.name}")

            // Set category name and color
            binding.tvCategoryName.text = category.name
            binding.tvCategoryName.setTextColor(android.graphics.Color.parseColor(category.colorHex))

            // Format and display spending amount (R15)
            val spendingFormatted = CurrencyUtils.formatMilliunits(category.totalMilliunits)
            binding.tvSpendingAmount.text = spendingFormatted

            // Calculate and display budget status (R16)
            val budgetStatus = CurrencyUtils.getBudgetStatus(category.totalMilliunits, category.maxGoalMilliunits)

            when (budgetStatus) {
                CurrencyUtils.BudgetStatus.GOOD -> {
                    binding.tvBudgetStatus.text = "Good"
                    binding.tvBudgetStatus.setTextColor(android.graphics.Color.parseColor("#16A34A")) // green
                    binding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#16A34A")
                    )
                }
                CurrencyUtils.BudgetStatus.CLOSE -> {
                    binding.tvBudgetStatus.text = "Close"
                    binding.tvBudgetStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B")) // amber
                    binding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#F59E0B")
                    )
                }
                CurrencyUtils.BudgetStatus.OVER -> {
                    binding.tvBudgetStatus.text = "Over!"
                    binding.tvBudgetStatus.setTextColor(android.graphics.Color.parseColor("#DC2626")) // red
                    binding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#DC2626")
                    )
                }
                CurrencyUtils.BudgetStatus.NO_GOAL -> {
                    binding.tvBudgetStatus.text = "No goal set"
                    binding.tvBudgetStatus.setTextColor(android.graphics.Color.parseColor("#6B7280")) // gray
                    binding.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#6B7280")
                    )
                }
            }

            // Set progress bar (R15)
            val progressFraction = CurrencyUtils.getProgressFraction(category.totalMilliunits, category.maxGoalMilliunits)
            binding.progressBar.progress = (progressFraction * 100).toInt()

            // Show goal amount if set
            if (category.maxGoalMilliunits > 0) {
                val goalFormatted = CurrencyUtils.formatMilliunits(category.maxGoalMilliunits)
                binding.tvGoalAmount.text = "Goal: $goalFormatted"
            } else {
                binding.tvGoalAmount.text = "No goal set"
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryWithSpending>() {
        override fun areItemsTheSame(oldItem: CategoryWithSpending, newItem: CategoryWithSpending): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: CategoryWithSpending, newItem: CategoryWithSpending): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Reference:
 * - Android Developers (2024) Create dynamic lists with RecyclerView. Google LLC.
 *   Available at: https://developer.android.com/develop/ui/views/layout/recyclerview (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.dashboard

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.R
import com.example.savesmart.databinding.ItemCategorySpendingBinding
import com.example.savesmart.util.BudgetStatus
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategorySpendingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(private val binding: ItemCategorySpendingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryWithSpending) {
            val context = itemView.context
            
            // Set category name and color
            binding.tvCategoryName.text = category.name
            try {
                binding.tvCategoryName.setTextColor(Color.parseColor(category.colorHex))
            } catch (e: Exception) {
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            }

            // Format and display spending amount
            binding.tvSpendingAmount.text = CurrencyUtils.formatMilliunits(category.totalMilliunits)

            // Calculate and display budget status
            val budgetStatus = CurrencyUtils.getBudgetStatus(category.totalMilliunits, category.maxGoalMilliunits)
            
            val statusColor = when (budgetStatus) {
                BudgetStatus.GOOD -> ContextCompat.getColor(context, R.color.good_green)
                BudgetStatus.CLOSE -> ContextCompat.getColor(context, R.color.close_amber)
                BudgetStatus.OVER -> ContextCompat.getColor(context, R.color.over_red)
                BudgetStatus.NO_GOAL -> ContextCompat.getColor(context, R.color.text_secondary)
            }

            val statusText = when (budgetStatus) {
                BudgetStatus.GOOD -> context.getString(R.string.status_good)
                BudgetStatus.CLOSE -> context.getString(R.string.status_close)
                BudgetStatus.OVER -> context.getString(R.string.status_over)
                BudgetStatus.NO_GOAL -> context.getString(R.string.status_no_goal)
            }

            binding.tvBudgetStatus.text = statusText
            binding.tvBudgetStatus.setTextColor(statusColor)
            binding.progressBar.progressTintList = ColorStateList.valueOf(statusColor)

            // Set progress bar
            val progressFraction = CurrencyUtils.getProgressFraction(category.totalMilliunits, category.maxGoalMilliunits)
            binding.progressBar.progress = (progressFraction * 100).toInt()

            // Show goal amount if set
            if (category.maxGoalMilliunits > 0) {
                val goalFormatted = CurrencyUtils.formatMilliunits(category.maxGoalMilliunits)
                binding.tvGoalAmount.text = context.getString(R.string.label_goal, goalFormatted)
            } else {
                binding.tvGoalAmount.text = context.getString(R.string.status_no_goal)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryWithSpending>() {
        override fun areItemsTheSame(oldItem: CategoryWithSpending, newItem: CategoryWithSpending) = 
            oldItem.categoryId == newItem.categoryId

        override fun areContentsTheSame(oldItem: CategoryWithSpending, newItem: CategoryWithSpending) = 
            oldItem == newItem
    }
}

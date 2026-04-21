/**
 * Reference:
 * - Android Developers (2024) ListAdapter. Google LLC.
 *   Available at: https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.databinding.ItemExpenseBinding
import com.example.savesmart.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying the list of expenses (Requirement R10, R11, R12).
 */
class ExpenseAdapter(
    private val onItemClicked: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvDescription.text = if (expense.description.isNotEmpty()) {
                expense.description
            } else {
                "No Description"
            }
            
            binding.tvAmount.text = CurrencyUtils.formatMilliunits(expense.amountMilliunits)
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(Date(expense.dateMillis))

            // R11: Click listener for details (receipt viewing)
            binding.root.setOnClickListener {
                onItemClicked(expense)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.expenseId == newItem.expenseId
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}

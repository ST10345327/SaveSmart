/**
 * Reference:
 * - Android Developers (2024) ListAdapter. Google LLC.
 *   Available at: https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.R
import com.example.savesmart.data.dao.ExpenseWithCategory
import com.example.savesmart.databinding.ItemExpenseBinding
import com.example.savesmart.util.CurrencyUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying the list of expenses (Requirement R10, R11, R12).
 */
class ExpenseAdapter(
    private val onItemClicked: (ExpenseWithCategory) -> Unit,
    private val onEditClicked: (ExpenseWithCategory) -> Unit,
    private val onDeleteClicked: (ExpenseWithCategory) -> Unit
) : ListAdapter<ExpenseWithCategory, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

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

        fun bind(expense: ExpenseWithCategory) {
            binding.tvDescription.text = if (expense.description.isNotEmpty()) {
                expense.description
            } else {
                "No Description"
            }
            
            binding.tvAmount.text = CurrencyUtils.formatMilliunits(expense.amountMilliunits)
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(Date(expense.dateMillis))

            // R11: Receipt thumbnail (Task 2)
            if (!expense.receiptPhotoPath.isNullOrEmpty()) {
                binding.ivReceiptThumbnail.visibility = View.VISIBLE
                val file = File(expense.receiptPhotoPath)
                if (file.exists()) {
                    binding.ivReceiptThumbnail.setImageURI(Uri.fromFile(file))
                } else {
                    binding.ivReceiptThumbnail.setImageResource(R.drawable.ic_expenses)
                }
            } else {
                binding.ivReceiptThumbnail.visibility = View.GONE
            }

            // Bind Category Data
            binding.tvCategoryName.text = expense.categoryName ?: "Uncategorized"
            try {
                val color = Color.parseColor(expense.categoryColor ?: "#9CA3AF")
                binding.viewCategoryColor.setBackgroundColor(color)
            } catch (e: Exception) {
                binding.viewCategoryColor.setBackgroundColor(Color.GRAY)
            }

            // R11: Click listener for details (receipt viewing)
            binding.root.setOnClickListener {
                onItemClicked(expense)
            }

            // Task 1: Edit/Delete menu
            binding.btnMore.setOnClickListener { view ->
                showPopupMenu(view, expense)
            }
        }

        private fun showPopupMenu(view: View, expense: ExpenseWithCategory) {
            val popup = PopupMenu(view.context, view)
            popup.menu.add(view.context.getString(R.string.action_edit))
            popup.menu.add(view.context.getString(R.string.action_delete))
            
            val editTitle = view.context.getString(R.string.action_edit)
            val deleteTitle = view.context.getString(R.string.action_delete)

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    editTitle -> {
                        onEditClicked(expense)
                        true
                    }
                    deleteTitle -> {
                        onDeleteClicked(expense)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExpenseWithCategory>() {
        override fun areItemsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {
            return oldItem.expenseId == newItem.expenseId
        }

        override fun areContentsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {
            return oldItem == newItem
        }
    }
}

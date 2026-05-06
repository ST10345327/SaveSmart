/**
 * Reference:
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
 * - Android Developers (2024) Room Persistence Library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentExpenseListBinding
import com.example.savesmart.util.SessionManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ExpenseListFragment — Displays a filtered list of expenses (Requirement R10).
 * Supports viewing details (R11) and deletion (R12).
 */
class ExpenseListFragment : Fragment() {

    private val TAG = "ExpenseListFragment"
    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter
    private lateinit var sessionManager: SessionManager

    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        setupRecyclerView()
        setupFilters()
        
        // Initialize with current month
        setDefaultDateRange()
        observeExpenses()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->
            Log.d(TAG, "Expense clicked: ${expense.expenseId}")
            if (!expense.receiptPhotoPath.isNullOrEmpty()) {
                val bundle = bundleOf("photoPath" to expense.receiptPhotoPath)
                findNavController().navigate(R.id.action_expenseListFragment_to_fullReceiptFragment, bundle)
            }
        }
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        // Requirement R12: Swipe to delete expense
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.currentList[position]
                showDeleteConfirmation(expense, position)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvExpenses)
    }

    private fun setupFilters() {
        // Requirement R12: Filter by date range
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun setDefaultDateRange() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        startDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        endDate = calendar.timeInMillis
        
        updateDateRangeText()
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select Date Range")
        val picker = builder.build()
        
        picker.addOnPositiveButtonClickListener { range ->
            startDate = range.first
            // Set end date to end of the selected day
            val cal = Calendar.getInstance()
            cal.timeInMillis = range.second
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            endDate = cal.timeInMillis
            
            updateDateRangeText()
            observeExpenses() // Refresh list
        }
        picker.show(childFragmentManager, "DATE_RANGE_PICKER")
    }

    private fun updateDateRangeText() {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateText = "${sdf.format(Date(startDate))} - ${sdf.format(Date(endDate))}"
        binding.tvDateRange.text = dateText
    }

    private fun observeExpenses() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModel.getExpensesLive(userId, startDate, endDate).observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvExpenses.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvExpenses.visibility = View.VISIBLE
                adapter.submitList(list)
            }
        }
    }

    private fun showDeleteConfirmation(expense: com.example.savesmart.data.entity.Expense, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteExpense(expense.expenseId)
                Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { _, _ ->
                adapter.notifyItemChanged(position)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

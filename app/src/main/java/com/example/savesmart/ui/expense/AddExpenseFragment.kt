/**
 * Reference:
 * - Android Developers (2024) DatePicker. Google LLC.
 *   Available at: https://developer.android.com/reference/android/app/DatePickerDialog (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.entity.Expense
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentAddExpenseBinding
import com.example.savesmart.util.CurrencyUtils
import com.example.savesmart.util.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment for adding new expense entries (Requirement R08).
 *
 * GitHub commit suggestion:
 *   [expense] implement AddExpenseFragment with validation and category selection
 *   - Integrated with Room via SaveSmartRepository
 *   - Added DatePicker and currency formatting
 *   Refs: R08, R10, R13, T10
 */
class AddExpenseFragment : Fragment() {

    private val TAG = "AddExpenseFragment"

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var sessionManager: SessionManager
    
    private var selectedDate = Calendar.getInstance()
    private var categoriesList = listOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = ExpenseViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupUI()
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadCategories(userId)
        }
    }

    private fun setupUI() {
        // Date Picker (R10)
        updateDateLabel()
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Save Button (R08, R13)
        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            Log.d(TAG, "observeViewModel(): Received ${categories.size} categories")
            categoriesList = categories
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.msg_expense_saved), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), getString(R.string.msg_expense_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, day)
                updateDateLabel()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateLabel() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(format.format(selectedDate.time))
    }

    /**
     * Requirement R13: Input validation before saving.
     */
    private fun validateAndSave() {
        val amountStr = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val categoryIndex = binding.spinnerCategory.selectedItemPosition

        if (amountStr.isEmpty()) {
            binding.etAmount.error = getString(R.string.err_enter_amount)
            return
        }

        val amountMilliunits = CurrencyUtils.parseRandInput(amountStr)
        if (amountMilliunits == null || amountMilliunits <= 0) {
            binding.etAmount.error = getString(R.string.err_invalid_amount)
            return
        }

        if (categoryIndex == -1 || categoriesList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.err_select_category), Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sessionManager.getUserId()
        val categoryId = categoriesList[categoryIndex].categoryId

        val expense = Expense(
            userId = userId,
            categoryId = categoryId,
            amountMilliunits = amountMilliunits,
            description = description,
            dateMillis = selectedDate.timeInMillis,
            startTimeMillis = selectedDate.timeInMillis, // Default to start of day or current
            endTimeMillis = selectedDate.timeInMillis + (1 * 60 * 60 * 1000) // Default 1 hour
        )

        Log.d(TAG, "validateAndSave(): Saving expense: $expense")
        viewModel.saveExpense(expense)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

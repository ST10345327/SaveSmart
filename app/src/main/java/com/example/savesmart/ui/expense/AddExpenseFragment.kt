/**
 * References:
 * - Android Developers (2024) DatePicker. Google LLC.
 *   Available at: https://developer.android.com/reference/android/app/DatePickerDialog (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - YNAB API (2024) YNAB API v1 documentation: milliunits and data model.
 *   Available at: https://api.ynab.com/v1 (Accessed: 24 March 2026).
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
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
 * AddExpenseFragment — Handles the creation of new expense records (Requirement R08).
 *
 * GitHub commit suggestion:
 *   [expense] finalize AddExpenseFragment with validation and navigation (R08, R13)
 *   - Implemented operationSuccess observer for feedback
 *   - Integrated with Room via SaveSmartRepository
 *   - Added strict logging and milliunit validation
 *   Refs: R08, R10, R13, T10
 */
class AddExpenseFragment : Fragment() {

    companion object {
        private const val TAG = "AddExpenseFragment"
    }

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
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = ExpenseViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupUI()
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            Log.d(TAG, "onViewCreated: loading categories for user $userId")
            viewModel.loadCategories(userId)
        } else {
            Log.w(TAG, "onViewCreated: no valid session found")
        }
    }

    private fun setupUI() {
        Log.d(TAG, "setupUI: initializing listeners")
        updateDateLabel()
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            Log.d(TAG, "observeViewModel: received ${categories.size} categories")
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
            Log.d(TAG, "observeViewModel: operationSuccess = $success")
            if (success) {
                // R08: Provide feedback and return to previous screen
                Toast.makeText(requireContext(), getString(R.string.msg_expense_saved), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                // R13: Handle failed save attempt
                Log.w(TAG, "observeViewModel: save operation failed")
                Toast.makeText(requireContext(), getString(R.string.msg_expense_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        Log.d(TAG, "showDatePicker: showing dialog")
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
     * Requirement T10: Milliunit currency storage.
     */
    private fun validateAndSave() {
        Log.d(TAG, "validateAndSave: validation started")
        val amountStr = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val categoryIndex = binding.spinnerCategory.selectedItemPosition

        if (amountStr.isEmpty()) {
            Log.w(TAG, "validateAndSave: empty amount")
            binding.etAmount.error = getString(R.string.err_enter_amount)
            return
        }

        // T10: Convert to milliunits (Long) to avoid floating point issues
        val amountMilliunits = CurrencyUtils.parseRandInput(amountStr)
        if (amountMilliunits == null || amountMilliunits <= 0) {
            Log.w(TAG, "validateAndSave: invalid amount — $amountStr")
            binding.etAmount.error = getString(R.string.err_invalid_amount)
            return
        }

        if (categoryIndex == -1 || categoriesList.isEmpty()) {
            Log.w(TAG, "validateAndSave: no category selected")
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
            startTimeMillis = selectedDate.timeInMillis,
            endTimeMillis = selectedDate.timeInMillis + (1 * 60 * 60 * 1000) 
        )

        Log.d(TAG, "validateAndSave: success — saving expense to repository")
        viewModel.saveExpense(expense)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: cleaning up binding")
        _binding = null
    }
}
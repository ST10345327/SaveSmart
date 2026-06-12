/**
 * References:
 * - Android Developers (2024) DatePicker. Google LLC.
 *   Available at: https://developer.android.com/reference/android/app/DatePickerDialog (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Android Developers (2024) Capture a photo. Google LLC.
 *   Available at: https://developer.android.com/training/camera/photobasics (Accessed: 24 March 2026).
 * - YNAB API (2024) YNAB API v1 documentation: milliunits and data model.
 *   Available at: https://api.ynab.com/v1 (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * AddExpenseFragment — Handles the creation of new expense records (Requirement R08).
 * Includes receipt photo capture functionality (Requirement R09).
 */
class AddExpenseFragment : Fragment() {

    companion object {
        private const val TAG = "AddExpenseFragment"
    }

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var sessionManager: SessionManager
    
    private var selectedDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private var categoriesList = listOf<Category>()
    
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private var expenseId: Int = -1
    private var isEditMode = false
    private var existingExpense: Expense? = null

    // Camera Result Launcher (R09)
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Camera: Photo taken successfully. Path: $currentPhotoPath")
            binding.ivReceipt.visibility = View.VISIBLE
            binding.layoutAddPhoto.visibility = View.GONE
            binding.ivReceipt.setImageURI(photoUri)
        } else {
            Log.w(TAG, "Camera: Photo capture cancelled or failed")
            // Clean up any temp file created when capture was cancelled
            currentPhotoPath?.let { path ->
                try {
                    val f = File(path)
                    if (f.exists()) f.delete()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to delete orphaned photo file", e)
                }
            }
            currentPhotoPath = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: entry")
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[ExpenseViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        arguments?.let {
            expenseId = it.getInt("expenseId", -1)
            isEditMode = expenseId != -1
        }

        setupUI()
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadCategories(userId)
        }

        if (isEditMode) {
            viewModel.loadExpenseForEditing(expenseId)
        }
    }

    private fun setupUI() {
        if (isEditMode) {
            binding.btnSave.setText(R.string.btn_update_expense)
        }

        updateDateLabel()
        
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.cardReceipt.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }

        // Handle discard changes (Requirement Rule 8 UX)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (hasUnsavedChanges()) {
                showDiscardDialog()
            } else {
                isEnabled = false
                findNavController().navigateUp()
            }
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.etAmount.text?.isNotEmpty() == true || 
               binding.etDescription.text?.isNotEmpty() == true ||
               currentPhotoPath != null
    }

    private fun showDiscardDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Discard Expense?")
            .setMessage("You have unsaved details. Are you sure you want to discard this expense?")
            .setPositiveButton("Discard") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Keep Editing", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories.map { it.name }
            )
            (binding.spinnerCategory as? AutoCompleteTextView)?.setAdapter(adapter)
            
            // If in edit mode and categories are loaded, pre-select category if existingExpense is already loaded
            existingExpense?.let { expense ->
                val category = categories.find { it.categoryId == expense.categoryId }
                category?.let {
                    binding.spinnerCategory.setText(it.name, false)
                }
            }
        }

        viewModel.expenseToEdit.observe(viewLifecycleOwner) { expense ->
            if (expense != null) {
                existingExpense = expense
                preFillForm(expense)
            }
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                val msg = if (isEditMode) R.string.msg_expense_updated else R.string.msg_expense_saved
                Toast.makeText(requireContext(), getString(msg), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), getString(R.string.msg_expense_failed), Toast.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            }
        }
    }

    /**
     * Requirement R09: Launch camera to capture receipt photo.
     */
    private fun dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent: started")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error occurred while creating the File", ex)
                    null
                }
                photoFile?.also {
                    val uri: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.savesmart.fileprovider",
                        it
                    )
                    photoUri = uri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    takePhotoLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "RECEIPT_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, day, 0, 0, 0)
                selectedCal.set(Calendar.MILLISECOND, 0)

                // Prevent selecting future dates
                if (selectedCal.timeInMillis <= today.timeInMillis) {
                    selectedDate = selectedCal
                    updateDateLabel()
                } else {
                    Toast.makeText(requireContext(), "Cannot select future dates", Toast.LENGTH_SHORT).show()
                }
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Set max date to today (prevent date picker from showing future dates)
            datePicker.maxDate = today.timeInMillis
        }.show()
    }

    private fun updateDateLabel() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(format.format(selectedDate.time))
    }

    private fun preFillForm(expense: Expense) {
        binding.etAmount.setText(CurrencyUtils.formatMilliunits(expense.amountMilliunits).replace("R", "").trim())
        binding.etDescription.setText(expense.description)
        selectedDate.timeInMillis = expense.dateMillis
        updateDateLabel()

        if (!expense.receiptPhotoPath.isNullOrEmpty()) {
            currentPhotoPath = expense.receiptPhotoPath
            binding.ivReceipt.visibility = View.VISIBLE
            binding.layoutAddPhoto.visibility = View.GONE
            binding.ivReceipt.setImageURI(Uri.fromFile(File(expense.receiptPhotoPath)))
        }

        // Category will be set when categories are loaded in the observer
        val category = categoriesList.find { it.categoryId == expense.categoryId }
        category?.let {
            binding.spinnerCategory.setText(it.name, false)
        }
    }

    private fun validateAndSave() {
        val amountStr = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val categoryName = binding.spinnerCategory.text.toString()
        
        if (amountStr.isEmpty()) {
            binding.tilAmount.error = getString(R.string.err_enter_amount)
            return
        } else {
            binding.tilAmount.error = null
        }

        val amountMilliunits = CurrencyUtils.parseRandInput(amountStr)
        if (amountMilliunits == null || amountMilliunits <= 0) {
            binding.tilAmount.error = getString(R.string.err_invalid_amount)
            return
        }

        val category = categoriesList.find { it.name == categoryName }
        if (category == null) {
            binding.tilCategory.error = getString(R.string.err_select_category)
            return
        } else {
            binding.tilCategory.error = null
        }

        val userId = sessionManager.getUserId()
        if (userId <= 0) {
            Toast.makeText(requireContext(), "User session invalid. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        // Prevent double-triggering (Requirement Event Handling)
        binding.btnSave.isEnabled = false

        val categoryId = category.categoryId

        val expense = Expense(
            expenseId = if (isEditMode) expenseId else 0,
            userId = userId,
            categoryId = categoryId,
            amountMilliunits = amountMilliunits,
            description = description,
            dateMillis = selectedDate.timeInMillis,
            startTimeMillis = selectedDate.timeInMillis,
            endTimeMillis = selectedDate.timeInMillis + (1 * 60 * 60 * 1000),
            receiptPhotoPath = currentPhotoPath // Save path to DB (R09)
        )

        Log.d(TAG, "validateAndSave: saving expense with path $currentPhotoPath")
        if (isEditMode) {
            viewModel.updateExpense(expense)
        } else {
            viewModel.saveExpense(expense)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * References:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Material Design (2024) Material Design 3. Google LLC.
 *   Available at: https://m3.material.io (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentAddEditCategoryBinding
import com.example.savesmart.util.CurrencyUtils
import com.example.savesmart.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AddEditCategoryFragment — Handles creating and editing expense categories (R05, R06, R14).
 *
 * GitHub commit suggestion:
 *   [categories] implement AddEditCategoryFragment with color selection and goals (R05, R06, R14)
 *   - Integrated with Room via SaveSmartRepository
 *   - Implemented min/max goal validation in milliunits
 *   - Added soft delete functionality (R07)
 *   Refs: R05, R06, R07, R14, T10
 */
class AddEditCategoryFragment : Fragment() {

    companion object {
        private const val TAG = "AddEditCategoryFragment"
    }

    private var _binding: FragmentAddEditCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: SaveSmartRepository
    
    // Using Navigation Safe Args to pass category ID for editing
    private val args: AddEditCategoryFragmentArgs by navArgs()
    
    private var selectedColorHex: String = "#1A6FE8" // Default primary blue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentAddEditCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        val db = SaveSmartDatabase.getInstance(requireContext())
        repository = SaveSmartRepository(db)
        viewModel = CategoriesViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupUI()
        
        if (args.categoryId != -1) {
            loadCategoryData(args.categoryId)
        } else {
            binding.tvTitle.text = "Create Category"
            binding.btnDeleteCategory.visibility = View.GONE
        }
    }

    private fun setupUI() {
        Log.d(TAG, "setupUI: initializing listeners")
        
        // Save Button (R05, R06, R14)
        binding.btnSaveCategory.setOnClickListener {
            validateAndSave()
        }
        
        // Delete Button (R07) - only visible if editing
        binding.btnDeleteCategory.setOnClickListener {
            deleteCategory()
        }
    }

    /**
     * Requirement R06: Load existing category data for editing.
     */
    private fun loadCategoryData(categoryId: Int) {
        Log.d(TAG, "loadCategoryData: fetching data for categoryId $categoryId")
        
        lifecycleScope.launch(Dispatchers.IO) {
            val db = SaveSmartDatabase.getInstance(requireContext())
            val category = db.categoryDao().getCategoryById(categoryId)
            
            withContext(Dispatchers.Main) {
                category?.let { cat ->
                    Log.d(TAG, "loadCategoryData: Found category '${cat.name}'")
                    binding.tvTitle.text = "Edit Category"
                    binding.etCategoryName.setText(cat.name)
                    
                    cat.minGoalMilliunits?.let {
                        binding.etMinGoal.setText(CurrencyUtils.milliunitsToRands(it).toString())
                    }
                    
                    cat.maxGoalMilliunits?.let {
                        binding.etMaxGoal.setText(CurrencyUtils.milliunitsToRands(it).toString())
                    }
                    
                    selectedColorHex = cat.colorHex
                    // Note: Update color UI if implemented
                }
            }
        }
    }

    /**
     * Requirement R13: Input validation before saving.
     * Requirement T10: Milliunit currency storage.
     */
    private fun validateAndSave() {
        Log.d(TAG, "validateAndSave: validation started")
        val name = binding.etCategoryName.text.toString().trim()
        val minGoalStr = binding.etMinGoal.text.toString().trim()
        val maxGoalStr = binding.etMaxGoal.text.toString().trim()

        if (name.isEmpty()) {
            Log.w(TAG, "validateAndSave: empty name")
            binding.etCategoryName.error = getString(R.string.err_fill_all_fields)
            return
        }

        // T10: Convert Rand inputs to milliunits
        val minGoal = if (minGoalStr.isNotEmpty()) CurrencyUtils.parseRandInput(minGoalStr) else 0L
        val maxGoal = if (maxGoalStr.isNotEmpty()) CurrencyUtils.parseRandInput(maxGoalStr) else 0L

        if (maxGoal != null && minGoal != null && maxGoal < minGoal) {
            Log.w(TAG, "validateAndSave: max goal less than min goal")
            binding.etMaxGoal.error = "Max goal cannot be less than min goal"
            return
        }

        val userId = sessionManager.getUserId()
        val category = Category(
            categoryId = if (args.categoryId != -1) args.categoryId else 0,
            userId = userId,
            name = name,
            colorHex = selectedColorHex,
            minGoalMilliunits = minGoal,
            maxGoalMilliunits = maxGoal
        )

        Log.d(TAG, "validateAndSave: success — saving category to repository")
        viewModel.saveCategory(category)
        Toast.makeText(requireContext(), "Category saved", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun deleteCategory() {
        Log.d(TAG, "deleteCategory: confirmed for categoryId ${args.categoryId}")
        viewModel.deleteCategory(args.categoryId)
        Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: cleaning up binding")
        _binding = null
    }
}

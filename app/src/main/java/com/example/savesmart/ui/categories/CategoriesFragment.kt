/**
 * References:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentCategoriesBinding
import com.example.savesmart.util.SessionManager

/**
 * CategoriesFragment — UI for managing spending categories.
 *
 * GitHub commit suggestion:
 *   [categories] implement CategoriesFragment with RecyclerView integration
 *   - Set up CategoryManagementAdapter for list display
 *   - Integrated navigation to Add/Edit screens
 *   Refs: R05, R06, T01, T06
 */
class CategoriesFragment : Fragment() {

    companion object {
        private const val TAG = "CategoriesFragment"
    }

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: CategoryManagementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        // Initialization (T01)
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = CategoriesViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: initializing adapter")
        adapter = CategoryManagementAdapter { category ->
            Log.d(TAG, "setupRecyclerView: navigating to edit category '${category.name}'")
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToAddEditCategoryFragment(category.categoryId)
            findNavController().navigate(action)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoriesFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.fabAddCategory.setOnClickListener {
            Log.d(TAG, "setupListeners: FAB clicked - navigating to AddCategory")
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToAddEditCategoryFragment(-1)
            findNavController().navigate(action)
        }
    }

    private fun observeData() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            Log.d(TAG, "observeData: observing categories for userId $userId")
            viewModel.getCategories(userId).observe(viewLifecycleOwner) { categories ->
                Log.d(TAG, "observeData: received ${categories.size} categories")
                adapter.submitList(categories)
            }
        } else {
            Log.w(TAG, "observeData: no valid userId found")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        // Requirement Rule 8: Prevent memory leaks (T06)
        _binding = null
    }
}

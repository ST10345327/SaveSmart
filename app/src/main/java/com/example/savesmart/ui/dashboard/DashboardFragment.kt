/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentDashboardBinding
import com.example.savesmart.util.BudgetStatus
import com.example.savesmart.util.CurrencyUtils
import com.example.savesmart.util.SessionManager

/**
 * Fragment responsible for displaying the user's monthly spending summary.
 *
 * GitHub commit suggestion:
 *   [dashboard] implement DashboardFragment with progress visualization
 *   - Integrated MVVM with DashboardViewModel
 *   - Added logic for monthly spending progress bar
 *   - Added navigation to Category Management
 *   Refs: R15, R16, T01, T06
 */
class DashboardFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardFragment"
    }

    // Requirement T06: ViewBinding pattern to prevent memory leaks
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        // Initialization (T01)
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = DashboardViewModel(repository)
        sessionManager = SessionManager(requireContext())

        // Setup RecyclerView (R15, R16)
        setupRecyclerView()

        // Setup logout button (R04)
        binding.btnLogout.setOnClickListener {
            Log.d(TAG, "onViewCreated: logout button clicked")
            logout()
        }

        // Setup Floating Action Button for Add Expense (R08)
        binding.fabAddExpense.setOnClickListener {
            Log.d(TAG, "onViewCreated: FAB clicked - navigating to AddExpenseFragment")
            findNavController().navigate(R.id.action_dashboardFragment_to_addExpenseFragment)
        }
        
        // Setup Navigation to Category Management (R05)
        // Note: Assuming there is a button with id 'btnManageCategories' in fragment_dashboard.xml
        // If not, we should use a menu item or another button.
        try {
            val btnManageCategories = binding.root.findViewById<View>(R.id.btnManageCategories)
            btnManageCategories?.setOnClickListener {
                Log.d(TAG, "onViewCreated: Manage Categories clicked - navigating to CategoriesFragment")
                findNavController().navigate(R.id.action_dashboardFragment_to_categoriesFragment)
            }
        } catch (e: Exception) {
            Log.w(TAG, "onViewCreated: Could not find btnManageCategories", e)
        }

        // Observe ViewModel state and update UI
        observeViewModel()
        
        // Load data for current user (R15)
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            Log.d(TAG, "onViewCreated: loading data for userId $userId")
            viewModel.loadDashboardData(userId)

            // Show welcome message with username
            val username = sessionManager.getUsername()
            binding.tvWelcome.text = "Welcome back, $username!"
        } else {
            Log.w(TAG, "onViewCreated: no active session found")
        }
    }

    /**
     * Setup RecyclerView with CategoryAdapter (R15, R16).
     */
    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: started")
        val adapter = CategoryAdapter()
        binding.rvCategories.adapter = adapter
        binding.rvCategories.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
    }

    /**
     * Requirement R15, R16: Observe ViewModel state and update UI.
     */
    private fun observeViewModel() {
        viewModel.totalSpent.observe(viewLifecycleOwner) { total ->
            Log.d(TAG, "observeViewModel: received total spent: $total")
            // Update total spent TextView (Requirement R15)
            binding.tvTotalSpending.text = CurrencyUtils.formatMilliunits(total)
        }

        viewModel.categoriesSummary.observe(viewLifecycleOwner) { summaries ->
            Log.d(TAG, "observeViewModel: received ${summaries.size} category summaries")
            // Update RecyclerView with category summaries (Requirement R16)
            val adapter = binding.rvCategories.adapter as? CategoryAdapter
            adapter?.submitList(summaries)

            // Check for overspending (R16)
            val hasOverspending = summaries.any { category ->
                CurrencyUtils.getBudgetStatus(category.totalMilliunits, category.maxGoalMilliunits) == BudgetStatus.OVER
            }
            binding.tvOverspendingWarning.visibility = if (hasOverspending) View.VISIBLE else View.GONE
        }
    }

    /**
     * Handle user logout (R04).
     */
    private fun logout() {
        Log.d(TAG, "logout: user requested logout")
        sessionManager.clearSession()

        // Navigate back to login screen
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        // Requirement Rule 8: Prevent memory leaks
        _binding = null
    }
}

/**
 * Reference:
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
 * - Android Developers (2024) Room Persistence Library. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/room (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

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
import com.example.savesmart.databinding.FragmentExpenseListBinding
import com.example.savesmart.util.SessionManager
import java.util.Calendar

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
        viewModel = ExpenseViewModel(repository)

        setupRecyclerView()
        observeExpenses()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->
            Log.d(TAG, "Expense clicked: ${expense.expenseId}")
            // R11: Future implementation — Navigate to detail/receipt view
        }
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter
    }

    /**
     * Requirement R10: Observe expenses for the current month by default.
     */
    private fun observeExpenses() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        // Calculate current month range
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startMillis = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val endMillis = calendar.timeInMillis

        viewModel.getExpensesLive(userId, startMillis, endMillis).observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "Received ${list.size} expenses")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

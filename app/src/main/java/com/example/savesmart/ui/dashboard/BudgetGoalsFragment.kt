package com.example.savesmart.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentBudgetGoalsBinding
import com.example.savesmart.util.CurrencyUtils
import com.example.savesmart.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetGoalsFragment : Fragment() {

    private val TAG = "BudgetGoalsFragment"
    private var _binding: FragmentBudgetGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)

        val userId = sessionManager.getUserId()
        if (userId == -1) {
            Toast.makeText(requireContext(), "No active user", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // Load existing goals and prefill
        CoroutineScope(Dispatchers.IO).launch {
            val user = repository.getUserById(userId)
            withContext(Dispatchers.Main) {
                user?.let {
                    if (it.minMonthlyBudget > 0L) binding.etMinGoal.setText(CurrencyUtils.formatMilliunits(it.minMonthlyBudget))
                    if (it.maxMonthlyBudget > 0L) binding.etMaxGoal.setText(CurrencyUtils.formatMilliunits(it.maxMonthlyBudget))
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val minStr = binding.etMinGoal.text.toString().trim()
            val maxStr = binding.etMaxGoal.text.toString().trim()

            val min = CurrencyUtils.parseRandInput(minStr)
            val max = CurrencyUtils.parseRandInput(maxStr)

            // Validation
            if (min == null || min < 0L) {
                Toast.makeText(requireContext(), "Please enter a valid minimum budget (>= 0)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (max == null || max < 0L) {
                Toast.makeText(requireContext(), "Please enter a valid maximum budget (>= 0)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (max < min) {
                Toast.makeText(requireContext(), "Maximum must be greater than or equal to minimum", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prevent double-triggering
            binding.btnSave.isEnabled = false

            // Persist to DB
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = repository.getUserById(userId)
                    user?.let {
                        val updated = it.copy(minMonthlyBudget = min, maxMonthlyBudget = max)
                        repository.updateUser(updated)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Budget goals saved", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving budget goals", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to save goals", Toast.LENGTH_SHORT).show()
                        binding.btnSave.isEnabled = true
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


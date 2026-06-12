/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Android Developers (2024) ViewPager2. Google LLC.
 *   Available at: https://developer.android.com/training/animation/screen-slide-2 (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentOnboardingBinding
import com.example.savesmart.ui.ViewModelFactory
import com.example.savesmart.util.CurrencyUtils
import com.example.savesmart.util.SessionManager
import com.google.android.material.tabs.TabLayoutMediator

/**
 * OnboardingFragment — 3-step onboarding flow (Requirement R23).
 * Step 1: Set monthly budget goals.
 * Step 2: Create first spending category.
 * Step 3: Explain rewards system.
 *
 * GitHub commit suggestion:
 *   [onboarding] implement 3-step setup flow for new users (R23)
 *   - Integrated budget and first category creation
 *   - Added ViewPager2 with progress indicators
 *   Refs: R23, T06, CS8
 */
class OnboardingFragment : Fragment() {

    private val TAG = "OnboardingFragment"
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: OnboardingViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: entry")
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[OnboardingViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        val adapter = OnboardingAdapter()
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Force use of buttons (Requirement R23)

        // Requirement R23: Load the last saved step for this user
        val userId = sessionManager.getUserId()
        if (userId > 0) {
            viewModel.loadOnboardingStep(userId)
        } else {
            // User session lost, guide back to login (Security UX)
            Toast.makeText(requireContext(), "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.loginFragment)
        }

        viewModel.onboardingStep.observe(viewLifecycleOwner) { step ->
            if (step in 0..2) {
                binding.viewPager.currentItem = step
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Handle back press to move between onboarding steps (Requirement R23 polish)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem -= 1
            } else {
                // If on first step, exit app
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.btnNext.setOnClickListener {
            val currentStep = binding.viewPager.currentItem
            val userId = sessionManager.getUserId()

            if (validateAndSaveStep(currentStep)) {
                if (currentStep < 2) {
                    val nextStep = currentStep + 1
                    binding.viewPager.currentItem = nextStep
                    viewModel.saveStepProgress(userId, nextStep)
                } else {
                    viewModel.completeOnboarding(userId)
                }
            }
        }

        binding.tvSkip.setOnClickListener {
            val userId = sessionManager.getUserId()
            // Do not mark onboarding complete until data is successfully saved
            if (userId <= 0) {
                Toast.makeText(requireContext(), "Unable to determine user. Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.completeOnboarding(userId)
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                sessionManager.setOnboardingComplete(true)
                findNavController().navigate(R.id.action_onboardingFragment_to_dashboardFragment)
            } else {
                Toast.makeText(requireContext(), "Error saving onboarding data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2) {
                    binding.btnNext.text = getString(R.string.onboarding_finish)
                } else {
                    binding.btnNext.text = getString(R.string.onboarding_next)
                }
            }
        })
    }

    /**
     * Requirement R23: Improved validation and data retrieval.
     * Moving away from fragile RecyclerView child access where possible.
     */
    private fun validateAndSaveStep(step: Int): Boolean {
        // Find the view for the current page
        val recyclerView = binding.viewPager.getChildAt(0) as? RecyclerView
        val currentView = recyclerView?.layoutManager?.findViewByPosition(step)
        
        if (currentView == null) {
            Log.e(TAG, "validateAndSaveStep: Could not find view for step $step")
            Toast.makeText(requireContext(), "Error accessing step data. Please try again.", Toast.LENGTH_SHORT).show()
            return false
        }

        when (step) {
            0 -> {
                val etMin = currentView.findViewById<EditText>(R.id.etMinGoal)
                val etMax = currentView.findViewById<EditText>(R.id.etMaxGoal)
                
                if (etMin == null || etMax == null) {
                    Log.e(TAG, "validateAndSaveStep: EditTexts not found in Step 1")
                    return false
                }

                val minStr = etMin.text.toString().trim()
                val maxStr = etMax.text.toString().trim()

                if (minStr.isEmpty() || maxStr.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.err_fill_all_fields), Toast.LENGTH_SHORT).show()
                    return false
                }

                val min = CurrencyUtils.parseRandInput(minStr)
                val max = CurrencyUtils.parseRandInput(maxStr)
                
                if (min == null || max == null) {
                    Toast.makeText(requireContext(), "Invalid amount entered", Toast.LENGTH_SHORT).show()
                    return false
                }

                if (max < min) {
                    Toast.makeText(requireContext(), "Max goal must be greater than or equal to min goal", Toast.LENGTH_SHORT).show()
                    return false
                }

                viewModel.minGoal = min
                viewModel.maxGoal = max
                return true
            }
            1 -> {
                val etCategory = currentView.findViewById<EditText>(R.id.etCategoryName)
                if (etCategory == null) {
                    Log.e(TAG, "validateAndSaveStep: EditText not found in Step 2")
                    return false
                }

                val name = etCategory.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
                    return false
                }
                viewModel.firstCategoryName = name
                return true
            }
            2 -> return true
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: cleanup")
        _binding = null
    }

    inner class OnboardingAdapter : RecyclerView.Adapter<OnboardingViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            val layout = when (viewType) {
                0 -> R.layout.item_onboarding_step1
                1 -> R.layout.item_onboarding_step2
                else -> R.layout.item_onboarding_step3
            }
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return OnboardingViewHolder(view)
        }

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
            when (position) {
                0 -> {
                    val etMin = holder.itemView.findViewById<EditText>(R.id.etMinGoal)
                    val etMax = holder.itemView.findViewById<EditText>(R.id.etMaxGoal)
                    if (viewModel.minGoal > 0) etMin?.setText(CurrencyUtils.formatMilliunitsNoCurrency(viewModel.minGoal))
                    if (viewModel.maxGoal > 0) etMax?.setText(CurrencyUtils.formatMilliunitsNoCurrency(viewModel.maxGoal))
                }
                1 -> {
                    val etCategory = holder.itemView.findViewById<EditText>(R.id.etCategoryName)
                    if (viewModel.firstCategoryName.isNotEmpty()) etCategory?.setText(viewModel.firstCategoryName)
                }
            }
        }

        override fun getItemCount(): Int = 3

        override fun getItemViewType(position: Int): Int = position
    }

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

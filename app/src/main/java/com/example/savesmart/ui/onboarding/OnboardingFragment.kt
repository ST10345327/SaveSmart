package com.example.savesmart.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
 */
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: OnboardingViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OnboardingViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        val adapter = OnboardingAdapter()
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Force use of buttons

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            val currentStep = binding.viewPager.currentItem
            if (validateAndSaveStep(currentStep)) {
                if (currentStep < 2) {
                    binding.viewPager.currentItem = currentStep + 1
                } else {
                    val userId = sessionManager.getUserId()
                    viewModel.completeOnboarding(userId)
                }
            }
        }

        binding.tvSkip.setOnClickListener {
            val userId = sessionManager.getUserId()
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

    private fun validateAndSaveStep(step: Int): Boolean {
        // We find the views within the ViewPager's current child
        val currentView = (binding.viewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(step)?.itemView ?: return false

        when (step) {
            0 -> {
                val etMin = currentView.findViewById<EditText>(R.id.etMinGoal)
                val etMax = currentView.findViewById<EditText>(R.id.etMaxGoal)
                
                val minStr = etMin?.text.toString()
                val maxStr = etMax?.text.toString()

                if (minStr.isNotEmpty() && maxStr.isNotEmpty()) {
                    val min = CurrencyUtils.parseRandInput(minStr)
                    val max = CurrencyUtils.parseRandInput(maxStr)
                    
                    if (min != null && max != null && max >= min) {
                        viewModel.minGoal = min
                        viewModel.maxGoal = max
                        return true
                    } else if (max != null && min != null && max < min) {
                        Toast.makeText(requireContext(), "Max goal must be greater than min goal", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
                // Allow empty goals for now if skip is an option, but here we require if they click Next
                Toast.makeText(requireContext(), "Please enter your budget goals", Toast.LENGTH_SHORT).show()
                return false
            }
            1 -> {
                val etCategory = currentView.findViewById<EditText>(R.id.etCategoryName)
                val name = etCategory?.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.firstCategoryName = name
                    return true
                }
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
                return false
            }
            2 -> return true
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {}

        override fun getItemCount(): Int = 3

        override fun getItemViewType(position: Int): Int = position
    }

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

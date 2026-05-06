/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.auth

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
import com.example.savesmart.databinding.FragmentRegisterBinding
import com.example.savesmart.util.SessionManager

/**
 * RegisterFragment — Handles new user registration flow (Requirement R01, R03, R23).
 *
 * Manages registration UI, input validation, password confirmation, and navigation
 * to Onboarding after successful account creation.
 *
 * GitHub commit suggestion:
 *   [ui] implement register fragment with password validation and account creation
 *   - Observes authState LiveData from AuthViewModel for reactive updates (T01)
 *   - Validates password strength (6+ chars, letters + numbers) (R03)
 *   - Prevents duplicate usernames via repository query (R01)
 *   - SessionManager saves new user login (R02)
 *   - Navigate to Onboarding after registration (R23)
 *   Refs: R01, R03, R23, T01, T06
 */
class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() entry")
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() entry")

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[AuthViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        // Clear fields when returning to this screen fresh
        if (savedInstanceState == null) {
            Log.d(TAG, "onViewCreated() clearing form fields")
            binding.etUsername.setText("")
            binding.etPassword.setText("")
            binding.etConfirmPassword.setText("")
        }

        setupListeners()
        observeViewModel()
        Log.d(TAG, "onViewCreated() complete")
    }

    private fun setupListeners() {
        Log.d(TAG, "setupListeners() entry")
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, getString(R.string.err_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, getString(R.string.err_passwords_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(username, password, confirmPassword)
        }

        binding.tvLoginLink.setOnClickListener {
            // Navigate to Login without keeping Register in back stack (R02, T01)
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    sessionManager.saveSession(result.user.userId, result.user.username)
                    sessionManager.setOnboardingComplete(false) // New user must onboard
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to Onboarding after registration (R23)
                    findNavController().navigate(R.id.action_registerFragment_to_onboardingFragment)
                }
                is AuthResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

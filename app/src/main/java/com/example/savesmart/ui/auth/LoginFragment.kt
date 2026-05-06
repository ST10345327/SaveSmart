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
import com.example.savesmart.databinding.FragmentLoginBinding
import com.example.savesmart.util.SessionManager

/**
 * LoginFragment — Handles user authentication screen (Requirement R02, R04).
 *
 * Manages login UI, credential input validation, session persistence, and navigation
 * to Dashboard on success or Onboarding on first registration.
 *
 * GitHub commit suggestion:
 *   [ui] implement login fragment with credential validation and session management
 *   - Observes authState LiveData from AuthViewModel for reactive updates (T01)
 *   - SessionManager saves user login for auto-login on restart (R02)
 *   - Navigate to Onboarding if first login (R23) or Dashboard if returning user
 *   - ViewBinding for memory-safe UI access (T06)
 *   Refs: R02, R04, R23, T01, T06
 */
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: entry")
        
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[AuthViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        // Clear fields for security only on fresh start (not rotation)
        if (savedInstanceState == null) {
            binding.etUsername.setText("")
            binding.etPassword.setText("")
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, getString(R.string.err_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d(TAG, "Login success for user: ${result.user.username}")
                    sessionManager.saveSession(result.user.userId, result.user.username)
                    
                    // Requirement R23: Update session manager with onboarding status
                    sessionManager.setOnboardingComplete(result.user.onboardingComplete)
                    
                    // BUG FIX: Check onboardingComplete from the database User object
                    if (result.user.onboardingComplete) {
                        Log.d(TAG, "User has completed onboarding. Navigating to Dashboard.")
                        findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                    } else {
                        Log.d(TAG, "New user detected. Navigating to Onboarding.")
                        findNavController().navigate(R.id.action_loginFragment_to_onboardingFragment)
                    }
                }
                is AuthResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: cleanup")
        _binding = null
    }
}

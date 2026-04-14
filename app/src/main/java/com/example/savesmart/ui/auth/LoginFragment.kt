/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Android Developers (2024) Navigation component. Google LLC.
 *   Available at: https://developer.android.com/guide/navigation (Accessed: 24 March 2026).
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
 * Fragment responsible for user login.
 * 
 * GitHub commit suggestion:
 *   [auth] convert LoginActivity to LoginFragment with ViewBinding
 *   - Implemented MVVM pattern with AuthViewModel
 *   - Integrated Navigation Component for screen transitions
 *   Refs: R02, T01, T06
 */
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    // Requirement T06: ViewBinding pattern to prevent memory leaks
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        // Initialization
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = AuthViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupListeners()
        observeViewModel()
    }

    /**
     * Requirement R02: Setup UI listeners for login action.
     */
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Requirement R03: Basic input validation
            if (username.isEmpty() || password.isEmpty()) {
                Log.w(TAG, "setupListeners(): Empty input fields detected")
                Toast.makeText(context, getString(R.string.err_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "setupListeners(): Initiating login for $username")
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            Log.d(TAG, "setupListeners(): Navigating to Registration")
            // Navigate using Navigation Component (Requirement: Navigation)
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    /**
     * Observe authentication state from ViewModel (T01).
     */
    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d(TAG, "observeViewModel(): Login successful for ${result.username}")
                    sessionManager.saveUser(result.username) // session management (R02)
                    
                    Toast.makeText(context, "Welcome back, ${result.username}!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to Dashboard
                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                }
                is AuthResult.Error -> {
                    Log.w(TAG, "observeViewModel(): Login failed - ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView(): Clearing binding")
        // Requirement Rule 8: Prevent memory leaks
        _binding = null
    }
}

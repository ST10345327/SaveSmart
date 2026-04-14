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
        
        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = AuthViewModel(repository)
        sessionManager = SessionManager(requireContext())

        // Clear fields when view is created/re-created (e.g., returning from Register)
        binding.etUsername.setText("")
        binding.etPassword.setText("")

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
                    sessionManager.saveSession(result.user.userId, result.user.username)
                    Toast.makeText(context, "Welcome back, ${result.user.username}!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
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

package com.example.savesmart.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.util.SecurityUtils
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user authentication (Requirement R01, R02, R03).
 */
class AuthViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Requirement R02: Authenticate user with credentials.
     * Normalized username and background hashing for performance (T08).
     */
    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthResult.Error("Username and password are required")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val normalizedUsername = username.lowercase().trim()
                val hashedPassword = SecurityUtils.hashPassword(password)
                if (hashedPassword.isEmpty()) {
                    _authState.value = AuthResult.Error("Login failed")
                    _isLoading.value = false
                    return@launch
                }

                val user = repository.loginUser(normalizedUsername, hashedPassword)
                
                if (user != null) {
                    _authState.value = AuthResult.Success(user)
                } else {
                    _authState.value = AuthResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Login failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Requirement R01, R03: Register a new user account.
     */
    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthResult.Error("Username and password are required")
            return
        }

        // Security Audit Fix: Professional validation for credentials
        if (!SecurityUtils.isValidUsername(username)) {
            _authState.value = AuthResult.Error("Username must be 3-20 characters (letters, numbers, underscores only)")
            return
        }

        if (!SecurityUtils.isValidPassword(password)) {
            _authState.value = AuthResult.Error("Password must be at least 6 characters with letters and numbers")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthResult.Error("Passwords do not match")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val normalizedUsername = username.lowercase().trim()
                
                val isTaken = repository.isUsernameTaken(normalizedUsername)
                if (isTaken) {
                    _authState.value = AuthResult.Error("Username already taken")
                    _isLoading.value = false
                    return@launch
                }

                val hashedPassword = SecurityUtils.hashPassword(password)
                if (hashedPassword.isEmpty()) {
                    _authState.value = AuthResult.Error("Registration failed")
                    _isLoading.value = false
                    return@launch
                }

                val success = repository.registerUser(normalizedUsername, hashedPassword)
                
                if (success) {
                    val newUser = repository.loginUser(normalizedUsername, hashedPassword)
                    if (newUser != null) {
                        _authState.value = AuthResult.Success(newUser)
                    } else {
                        _authState.value = AuthResult.Error("Account created, please log in")
                    }
                } else {
                    _authState.value = AuthResult.Error("Registration failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "register() error", e)
                _authState.value = AuthResult.Error("Registration error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AuthResult {
    data class Success(val user: com.example.savesmart.data.entity.User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

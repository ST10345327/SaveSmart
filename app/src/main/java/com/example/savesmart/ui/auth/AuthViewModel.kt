/**
 * Reference:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 * - Android Developers (2024) Security with SHA-256. Google LLC.
 *   Available at: https://developer.android.com/training/articles/security-tips (Accessed: 24 March 2026).
 */

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
 *
 * GitHub commit suggestion:
 *   [auth] implement AuthViewModel with SHA-256 hashing and session logic
 *   - Integrated with SaveSmartRepository for user CRUD
 *   - Implemented password validation and duplicate check
 *   Refs: R01, R02, R03, T01
 */
class AuthViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    /**
     * Requirement R02: Authenticate user with credentials.
     */
    fun login(username: String, password: String) {
        Log.d(TAG, "login() attempt for username: $username")
        
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthResult.Error("Username and password are required")
            return
        }

        viewModelScope.launch {
            try {
                val hashedPassword = SecurityUtils.hashPassword(password)
                val user = repository.loginUser(username, hashedPassword)
                
                if (user != null) {
                    Log.d(TAG, "login() success for user: $username")
                    _authState.value = AuthResult.Success(user)
                } else {
                    Log.w(TAG, "login() failed: Invalid credentials for $username")
                    _authState.value = AuthResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                Log.e(TAG, "login() error: ${e.message}", e)
                _authState.value = AuthResult.Error("Login failed: ${e.message}")
            }
        }
    }

    /**
     * Requirement R01, R03: Register a new user account.
     */
    fun register(username: String, password: String, confirmPassword: String) {
        Log.d(TAG, "register() attempt for username: $username")

        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthResult.Error("Username and password are required")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthResult.Error("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthResult.Error("Passwords do not match")
            return
        }

        // Added security check: Password should not be the same as username
        if (password.lowercase() == username.lowercase()) {
            _authState.value = AuthResult.Error("Password cannot be the same as username")
            return
        }

        viewModelScope.launch {
            try {
                val isTaken = repository.isUsernameTaken(username)
                if (isTaken) {
                    Log.w(TAG, "register() failed: Username '$username' is already taken")
                    _authState.value = AuthResult.Error("Username already taken")
                    return@launch
                }

                val hashedPassword = SecurityUtils.hashPassword(password)
                val success = repository.registerUser(username, hashedPassword)
                
                if (success) {
                    Log.d(TAG, "register() success: User '$username' created")
                    val newUser = repository.loginUser(username, hashedPassword)
                    if (newUser != null) {
                        _authState.value = AuthResult.Success(newUser)
                    } else {
                        _authState.value = AuthResult.Error("Registration succeeded but login failed")
                    }
                } else {
                    Log.e(TAG, "register() failed: Repository returned false")
                    _authState.value = AuthResult.Error("Registration failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "register() error: ${e.message}", e)
                _authState.value = AuthResult.Error("Registration error: ${e.message}")
            }
        }
    }
}

/**
 * Sealed class representing authentication outcomes.
 */
sealed class AuthResult {
    data class Success(val user: com.example.savesmart.data.entity.User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

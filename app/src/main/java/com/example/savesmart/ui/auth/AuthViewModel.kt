/**
 * Reference:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 * - Android Developers (2024) Kotlin coroutines on Android. Google LLC.
 *   Available at: https://developer.android.com/kotlin/coroutines (Accessed: 24 March 2026).
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
 * ViewModel for handling authentication logic.
 *
 * GitHub commit suggestion:
 *   [auth] implement login and registration logic in AuthViewModel
 *   - Added SHA-256 hashing for passwords
 *   - Implemented viewModelScope for database operations
 *   Refs: R01, R02, T01
 */
class AuthViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    /**
     * Authenticates a user with hashed password validation (R02).
     */
    fun login(username: String, password: String) {
        Log.d(TAG, "login() called for user: $username")
        viewModelScope.launch {
            try {
                val hashedPassword = SecurityUtils.hashPassword(password)
                val user = repository.loginUser(username, hashedPassword)
                
                if (user != null) {
                    Log.d(TAG, "login() success for user: $username")
                    _authState.value = AuthResult.Success(username)
                } else {
                    Log.w(TAG, "login() failed: Invalid credentials for $username")
                    _authState.value = AuthResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                Log.e(TAG, "login() unexpected error", e)
                _authState.value = AuthResult.Error("An unexpected error occurred")
            } finally {
                Log.d(TAG, "login() execution completed")
            }
        }
    }

    /**
     * Registers a new user with SHA-256 hashed password (R01).
     */
    fun register(username: String, password: String) {
        Log.d(TAG, "register() called for user: $username")
        viewModelScope.launch {
            try {
                val hashedPassword = SecurityUtils.hashPassword(password)
                val success = repository.registerUser(username, hashedPassword)
                
                if (success) {
                    Log.d(TAG, "register() success for user: $username")
                    _authState.value = AuthResult.Success(username)
                } else {
                    Log.w(TAG, "register() failed: User $username already exists")
                    _authState.value = AuthResult.Error("User already exists")
                }
            } catch (e: Exception) {
                Log.e(TAG, "register() unexpected error", e)
                _authState.value = AuthResult.Error("Registration failed")
            }
        }
    }
}

/**
 * Sealed class to represent authentication states.
 */
sealed class AuthResult {
    data class Success(val username: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

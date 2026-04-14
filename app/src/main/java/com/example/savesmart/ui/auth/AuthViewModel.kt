package com.example.savesmart.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.util.SecurityUtils
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthResult.Error("Username and password are required")
            return
        }

        viewModelScope.launch {
            try {
                val hashedPassword = SecurityUtils.hashPassword(password)
                val user = repository.loginUser(username, hashedPassword)
                
                if (user != null) {
                    _authState.value = AuthResult.Success(user)
                } else {
                    _authState.value = AuthResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String, confirmPassword: String) {
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
                    _authState.value = AuthResult.Error("Username already taken")
                    return@launch
                }

                val hashedPassword = SecurityUtils.hashPassword(password)
                val success = repository.registerUser(username, hashedPassword)
                
                if (success) {
                    val newUser = repository.loginUser(username, hashedPassword)
                    if (newUser != null) {
                        _authState.value = AuthResult.Success(newUser)
                    } else {
                        _authState.value = AuthResult.Error("Registration succeeded but login failed")
                    }
                } else {
                    _authState.value = AuthResult.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Registration error: ${e.message}")
            }
        }
    }
}

sealed class AuthResult {
    data class Success(val user: com.example.savesmart.data.entity.User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

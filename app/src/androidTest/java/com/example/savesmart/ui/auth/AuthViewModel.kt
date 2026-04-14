package com.savesmart.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savesmart.data.repository.SaveSmartRepository
import com.savesmart.util.SecurityUtils
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    // Register user
    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Validate input
            if (!SecurityUtils.isValidUsername(username) ||
                !SecurityUtils.isValidPassword(password)) {
                onResult(false)
                return@launch
            }

            val hashedPassword = SecurityUtils.hashPassword(password)

            val success = repository.registerUser(username, hashedPassword)
            onResult(success)
        }
    }

    // Login user
    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hashedPassword = SecurityUtils.hashPassword(password)

            val user = repository.loginUser(username, hashedPassword)

            onResult(user != null)
        }
    }
}
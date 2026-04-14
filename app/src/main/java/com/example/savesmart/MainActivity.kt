/**
 * Reference:
 * - Android Developers (2024) Activity overview. Google LLC.
 *   https://developer.android.com/guide/components/activities
 * - Android Developers (2024) Navigation component. Google LLC.
 *   https://developer.android.com/guide/navigation
 * - Android Developers (2024) Kotlin coroutines on Android. Google LLC.
 *   https://developer.android.com/kotlin/coroutines
 */

package com.example.savesmart

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.savesmart.util.SessionManager

/**
 * Main entry point activity with auto-login logic.
 *
 * GitHub commit suggestion:
 *   [auth] Implement MainActivity with auto-login session check
 *   - Check if user session exists on app launch
 *   - Auto-login if session valid, otherwise show login screen
 *   - Handle session restoration and UI navigation
 *   Refs: R02, R04, T01
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing MainActivity")

        setContentView(R.layout.activity_main)

        try {
            // Handle edge-to-edge display
            val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main_layout)
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Initialize session manager
            sessionManager = SessionManager(this)
            Log.d(TAG, "onCreate: SessionManager initialized")

            // Setup navigation
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            Log.d(TAG, "onCreate: Navigation setup complete")

            // Check if user is logged in (R02)
            checkAutoLogin()

        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error during initialization", e)
        }
    }

    /**
     * Check if user has active session and auto-login if available (R02).
     * If no session, show login screen.
     * If session exists, navigate to dashboard directly.
     */
    private fun checkAutoLogin() {
        Log.d(TAG, "checkAutoLogin: Checking for active session")

        val isLoggedIn = sessionManager.isLoggedIn()
        val userId = sessionManager.getUserId()
        val username = sessionManager.getUsername()

        Log.d(TAG, "checkAutoLogin: isLoggedIn=$isLoggedIn, userId=$userId, username=$username")

        if (isLoggedIn && userId > 0) {
            Log.d(TAG, "checkAutoLogin: Session found - auto-logging in user (ID=$userId, username=$username)")
            // User is already logged in - navigate to dashboard directly
            navigateToDashboard()
        } else {
            Log.d(TAG, "checkAutoLogin: No session found - showing login screen")
            // No session - show login screen (default start destination)
        }
    }

    /**
     * Navigate to dashboard after successful auth or auto-login (R02).
     */
    private fun navigateToDashboard() {
        try {
            Log.d(TAG, "navigateToDashboard: Navigating to dashboard")
            navController.navigate(R.id.action_loginFragment_to_dashboardFragment)
            Log.d(TAG, "navigateToDashboard: Navigation complete")
        } catch (e: Exception) {
            Log.e(TAG, "navigateToDashboard: Navigation failed", e)
        }
    }

    /**
     * Logout current user and show login screen (R04).
     */
    fun logout() {
        Log.d(TAG, "logout: Clearing session and returning to login")
        sessionManager.clearSession()

        // Navigate back to login
        navController.navigate(R.id.loginFragment)
    }
}
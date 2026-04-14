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

            // Clean up legacy session data to prevent auto-login bugs (T01)
            validateAndCleanSession()

            // Setup navigation
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            Log.d(TAG, "onCreate: Navigation setup complete")

            // Delay auto-login check to allow fragments to initialize
            mainLayout.post {
                checkAutoLogin()
            }

        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error during initialization", e)
        }
    }

    /**
     * Validate session data and clean up if corrupted.
     * Prevents jumping to dashboard when session data is invalid or leftover from tests.
     */
    private fun validateAndCleanSession() {
        Log.d(TAG, "validateAndCleanSession: Checking for invalid session data")

        val isLoggedIn = sessionManager.isLoggedIn()
        val userId = sessionManager.getUserId()
        val username = sessionManager.getUsername()

        Log.d(TAG, "validateAndCleanSession: Current SharedPrefs state:")
        Log.d(TAG, "  - isLoggedIn: $isLoggedIn")
        Log.d(TAG, "  - userId: $userId")
        Log.d(TAG, "  - username: '$username'")

        // If session claims to be logged in but lacks valid user data, clear it (T01)
        // This handles corrupted sessions from previous test runs
        if (isLoggedIn && (userId <= 0 || username.isNullOrEmpty())) {
            Log.w(TAG, "validateAndCleanSession: ⚠️ Found corrupted session data - clearing it")
            Log.w(TAG, "  - isLoggedIn=$isLoggedIn, userId=$userId, username='$username'")
            sessionManager.clearSession()
            Log.d(TAG, "validateAndCleanSession: ✅ Corrupted session cleared - user will see login screen")
        } else if (isLoggedIn && userId > 0 && !username.isNullOrEmpty()) {
            Log.d(TAG, "validateAndCleanSession: ✅ Session data is valid - will auto-login")
        } else {
            Log.d(TAG, "validateAndCleanSession: No active session - user will see login screen")
        }
    }

    /**
     * Check if user has active session and auto-login if available (R02).
     * If no session, show login screen (default start destination).
     * If session exists and is valid, navigate to dashboard directly.
     */
    private fun checkAutoLogin() {
        Log.d(TAG, "checkAutoLogin: Starting session validation...")

        val isLoggedIn = sessionManager.isLoggedIn()
        val userId = sessionManager.getUserId()
        val username = sessionManager.getUsername()

        Log.d(TAG, "checkAutoLogin: Raw values - isLoggedIn=$isLoggedIn, userId=$userId, username='$username'")

        // Strict validation: ALL conditions must be true (R02, T01)
        val hasValidSession = isLoggedIn && userId > 0 && !username.isNullOrEmpty()

        Log.d(TAG, "checkAutoLogin: Session validation result = $hasValidSession")

        if (hasValidSession) {
            Log.d(TAG, "checkAutoLogin: ✅ Valid session found - auto-logging in user (ID=$userId, username=$username)")
            try {
                // Navigate to dashboard and remove login from back stack
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                navController.navigate(R.id.dashboardFragment, null, navOptions)
                Log.d(TAG, "checkAutoLogin: ✅ Successfully navigated to Dashboard")
            } catch (e: Exception) {
                Log.e(TAG, "checkAutoLogin: ❌ Navigation failed", e)
            }
        } else {
            Log.d(TAG, "checkAutoLogin: ❌ No valid session - user will see login screen")
            // Do NOT navigate - let nav_graph show login screen as default start destination
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
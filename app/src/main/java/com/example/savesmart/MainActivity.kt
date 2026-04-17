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
 * Main entry point activity.
 *
 * GitHub commit suggestion:
 *   [auth] Update MainActivity to always show login screen on startup
 *   - Removed auto-login logic to ensure consistent app entry
 *   - Maintained session management for logout functionality
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

            // Note: Auto-login check removed to fix issue where app skips login screen (T01)
            // The app will now always start at the destination defined in nav_graph.xml (LoginFragment)

        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error during initialization", e)
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

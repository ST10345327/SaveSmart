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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.savesmart.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main entry point activity.
 *
 * GitHub commit suggestion:
 *   [ui] implement BottomNavigationView with Navigation component
 *   - Integrated BottomNavigationView with NavController
 *   - Added visibility logic for auth vs main screens
 *   Refs: R05, R10, R15, R17
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SaveSmart)
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

            // AUTO-LOGIN & ONBOARDING CHECK (Requirement R23)
            if (savedInstanceState == null && sessionManager.isLoggedIn()) {
                val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
                
                if (sessionManager.isOnboardingComplete()) {
                    Log.d(TAG, "onCreate: User logged in and onboarding complete. Starting at Dashboard.")
                    navGraph.setStartDestination(R.id.dashboardFragment)
                } else {
                    Log.d(TAG, "onCreate: User logged in but onboarding NOT complete. Starting at Onboarding.")
                    navGraph.setStartDestination(R.id.onboardingFragment)
                }
                navController.graph = navGraph
            }
            
            // Setup Bottom Navigation (Requirement R05, R10, R15, R17)
            bottomNav = findViewById(R.id.bottom_navigation)
            bottomNav.setupWithNavController(navController)

            // Show/Hide bottom navigation based on destination
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.loginFragment, R.id.registerFragment -> {
                        bottomNav.visibility = View.GONE
                    }
                    else -> {
                        bottomNav.visibility = View.VISIBLE
                    }
                }
            }

            Log.d(TAG, "onCreate: Navigation setup complete")

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

        // Navigate back to login and clear backstack
        navController.navigate(
            R.id.loginFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }
}

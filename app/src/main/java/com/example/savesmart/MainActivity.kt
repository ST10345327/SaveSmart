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
import androidx.lifecycle.lifecycleScope
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

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
                // Only apply horizontal and top padding to the root layout
                // Bottom padding is handled by the BottomNavigationView itself
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
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
                val userId = sessionManager.getUserId()
                lifecycleScope.launch {
                    val db = SaveSmartDatabase.getInstance(this@MainActivity)
                    val user = db.userDao().getUserById(userId)
                    
                    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
                    
                    if (user != null) {
                        // Sync SessionManager with DB truth
                        sessionManager.setOnboardingComplete(user.onboardingComplete)
                        
                        if (user.onboardingComplete) {
                            Log.d(TAG, "onCreate: User logged in and onboarding complete. Starting at Dashboard.")
                            navGraph.setStartDestination(R.id.dashboardFragment)
                        } else {
                            Log.d(TAG, "onCreate: User logged in but onboarding NOT complete. Starting at Onboarding.")
                            navGraph.setStartDestination(R.id.onboardingFragment)
                        }
                    } else {
                        // User ID in session doesn't exist in DB (corrupted session)
                        Log.w(TAG, "onCreate: Session user ID $userId not found in DB. Clearing session.")
                        sessionManager.clearSession()
                        navGraph.setStartDestination(R.id.loginFragment)
                    }
                    navController.graph = navGraph
                }
            }
            
            // Setup Bottom Navigation (Requirement R05, R10, R15, R17)
            bottomNav = findViewById(R.id.bottom_navigation)
            bottomNav.setupWithNavController(navController)

            // NAVIGATION GUARD & UI LOGIC (Requirement R23 Security)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                // 1. Guard against unauthenticated access
                if (!sessionManager.isLoggedIn() && 
                    destination.id != R.id.loginFragment && 
                    destination.id != R.id.registerFragment) {
                    Log.w(TAG, "NavigationGuard: Unauthenticated access to ${destination.label}. Redirecting to Login.")
                    navController.navigate(R.id.loginFragment)
                    return@addOnDestinationChangedListener
                }

                // 2. Guard against bypassing onboarding
                if (sessionManager.isLoggedIn() && 
                    !sessionManager.isOnboardingComplete() && 
                    destination.id != R.id.onboardingFragment &&
                    destination.id != R.id.loginFragment) {
                    Log.w(TAG, "NavigationGuard: Onboarding incomplete. Redirecting from ${destination.label} to Onboarding.")
                    navController.navigate(R.id.onboardingFragment)
                    return@addOnDestinationChangedListener
                }

                // 3. UI visibility logic
                when (destination.id) {
                    R.id.loginFragment, R.id.registerFragment, R.id.onboardingFragment,
                    R.id.addExpenseFragment, R.id.addEditCategoryFragment,
                    R.id.fullReceiptFragment, R.id.budgetGoalsFragment,
                    R.id.leaderboardFragment -> {
                        bottomNav.visibility = View.GONE
                        bottomNav.isEnabled = false
                    }
                    else -> {
                        bottomNav.visibility = View.VISIBLE
                        bottomNav.isEnabled = true
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

        // Clear back stack and navigate to login safely
        val startId = navController.graph.startDestinationId
        val navOptions = androidx.navigation.NavOptions.Builder()
            .setPopUpTo(startId, true)
            .build()
        navController.navigate(R.id.loginFragment, null, navOptions)
    }
}

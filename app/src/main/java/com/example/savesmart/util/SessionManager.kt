/**
 * Reference:
 * - Android Developers (2024) Shared Preferences. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/shared-preferences (Accessed: 24 March 2026).
 */

package com.example.savesmart.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * SessionManager — Handles user session persistence via SharedPreferences (Requirement R02, R04).
 * Stores user ID, username, and login status for auto-login on app restart.
 *
 * GitHub commit suggestion:
 *   [util] implement session manager for persistent user login
 *   - saveSession() persists user login across app restart
 *   - clearSession() on logout removes all session data
 *   - isLoggedIn() polls current session state
 *   - Onboarding completion flag stored separately
 *   Refs: R02, R04, T01
 */
class SessionManager(context: Context) {

    private val TAG = "SessionManager"
    private val PREF_NAME = "savesmart_session"
    private val KEY_USER_ID   = "user_id"
    private val KEY_USERNAME  = "username"
    private val KEY_IS_LOGGED = "is_logged_in"
    private val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Save user session (Requirement R02).
     * @param userId database user ID
     * @param username user's username
     */
    fun saveSession(userId: Int, username: String) {
        Log.d(TAG, "saveSession() entry — userId: $userId")
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
        Log.d(TAG, "saveSession() complete")
    }

    /**
     * Save user without full session (partial login).
     * @param username user's username
     */
    fun saveUser(username: String) {
        Log.d(TAG, "saveUser() entry — username: $username")
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
        Log.d(TAG, "saveUser() complete")
    }

    /**
     * Get stored user ID from session.
     * @return user ID, or -1 if not found
     */
    fun getUserId(): Int {
        val userId = prefs.getInt(KEY_USER_ID, -1)
        Log.d(TAG, "getUserId() returning: $userId")
        return userId
    }

    /**
     * Get stored username from session.
     * @return username string, or null if not found
     */
    fun getUsername(): String? {
        val username = prefs.getString(KEY_USERNAME, null)
        Log.d(TAG, "getUsername() returning: $username")
        return username
    }

    /**
     * Check if user is currently logged in (Requirement R02).
     * @return true if session exists, false otherwise
     */
    fun isLoggedIn(): Boolean {
        val isLogged = prefs.getBoolean(KEY_IS_LOGGED, false)
        Log.d(TAG, "isLoggedIn() returning: $isLogged")
        return isLogged
    }

    /**
     * Mark onboarding as complete (Requirement R23).
     * @param complete true to mark complete, false to reset
     */
    fun setOnboardingComplete(complete: Boolean) {
        Log.d(TAG, "setOnboardingComplete() entry — complete: $complete")
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply()
        Log.d(TAG, "setOnboardingComplete() complete")
    }

    /**
     * Check if onboarding has been completed (Requirement R23).
     * @return true if completed, false otherwise
     */
    fun isOnboardingComplete(): Boolean {
        val complete = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        Log.d(TAG, "isOnboardingComplete() returning: $complete")
        return complete
    }

    /**
     * Clear all session data on logout (Requirement R04).
     * Prevents re-access to previous user's data.
     */
    fun clearSession() {
        Log.d(TAG, "clearSession() entry")
        prefs.edit().clear().apply()
        Log.d(TAG, "clearSession() complete — session cleared")
    }
}
package com.savesmart.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * SessionManager — Manages user session using SharedPreferences (R02, R04).
 *
 * References:
 * - Android Developers (2024) Save key-value data with SharedPreferences. Google LLC.
 *   Available at: https://developer.android.com/training/data-storage/shared-preferences
 *   (Accessed: 24 March 2026).
 *
 * Design decisions:
 * - SharedPreferences used for lightweight session storage (R02).
 * - Only userId and username stored — no sensitive data in SharedPreferences.
 * - Passwords and hashes are NEVER stored in SharedPreferences.
 * - Session is cleared on logout (R04).
 * - Singleton pattern ensures consistent session state across the app.
 */
class SessionManager(context: Context) {

    private val TAG = "SessionManager"

    /** SharedPreferences file name. */
    private val PREF_NAME = "savesmart_session"

    /** SharedPreferences keys. */
    private val KEY_USER_ID   = "user_id"
    private val KEY_USERNAME  = "username"
    private val KEY_IS_LOGGED = "is_logged_in"

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ── Session operations ────────────────────────────────────────────────────

    /**
     * Saves the authenticated user's session (R02).
     * Called immediately after successful login or registration.
     *
     * @param userId The authenticated user's primary key.
     * @param username The authenticated user's username.
     */
    fun saveSession(userId: Int, username: String) {
        Log.d(TAG, "Saving session for userId: $userId, username: $username")
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
    }

    /**
     * Returns the currently logged-in user's ID (R02).
     * Returns -1 if no user is logged in.
     *
     * @return The logged-in user's ID, or -1.
     */
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    /**
     * Returns the currently logged-in user's username (R02).
     * Returns null if no user is logged in.
     *
     * @return The logged-in username, or null.
     */
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    /**
     * Returns whether a user is currently logged in (R02).
     *
     * @return True if a session is active.
     */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED, false)

    /**
     * Clears all session data on logout (R04).
     * Called when the user taps the Logout button.
     * After calling this, isLoggedIn() returns false and getUserId() returns -1.
     */
    fun clearSession() {
        Log.d(TAG, "Clearing session — user logged out")
        prefs.edit().clear().apply()
    }
}

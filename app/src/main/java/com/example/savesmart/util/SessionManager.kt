package com.example.savesmart.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    private val TAG = "SessionManager"
    private val PREF_NAME = "savesmart_session"
    private val KEY_USER_ID   = "user_id"
    private val KEY_USERNAME  = "username"
    private val KEY_IS_LOGGED = "is_logged_in"

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Int, username: String) {
        Log.d(TAG, "Saving session for userId: $userId, username: $username")
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
    }

    fun saveUser(username: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_IS_LOGGED, true)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED, false)

    fun clearSession() {
        Log.d(TAG, "Clearing session — user logged out")
        prefs.edit().clear().apply()
    }
}
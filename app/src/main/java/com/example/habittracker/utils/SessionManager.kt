package com.example.habittracker.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? = preferences.getString(KEY_AUTH_TOKEN, null)

    fun saveRefreshToken(token: String) {
        preferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun fetchRefreshToken(): String? = preferences.getString(KEY_REFRESH_TOKEN, null)

    fun clearAuthToken() {
        preferences.edit().remove(KEY_AUTH_TOKEN).apply()
        preferences.edit().remove(KEY_REFRESH_TOKEN).apply()
        preferences.edit().remove(KEY_USER_ID).apply()
    }

    fun saveUserId(userId: Int) {
        preferences.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun fetchUserId(): Int? {
        val userId = preferences.getInt(KEY_USER_ID, -1)
        return if (userId >= 0) userId else null
    }

    private companion object {
        const val PREFERENCES_NAME = "auth_prefs"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}

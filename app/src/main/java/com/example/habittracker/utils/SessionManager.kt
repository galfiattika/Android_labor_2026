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

    fun clearAuthToken() {
        preferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "auth_prefs"
        const val KEY_AUTH_TOKEN = "auth_token"
    }
}

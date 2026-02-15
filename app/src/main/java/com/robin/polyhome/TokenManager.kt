package com.robin.polyhome

import android.content.Context

object TokenManager {
    private const val PREFS_NAME = "Polyhome_Prefs"
    private const val KEY_TOKEN = "auth_token"


    fun saveToken(context: Context, token: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
}
package com.robin.polyhome

import android.content.Context
import kotlin.coroutines.Continuation

object TokenManager {
    private const val PREFS_NAME = "Polyhome_Prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_LOGIN = "login_username"

    fun saveToken(context: Context, token: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token =  sharedPreferences.getString(KEY_TOKEN, null)
        return if (token.isNullOrEmpty()) null else token
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun saveLogin(context: Context, login: String){
        val sharedPreferencesLogin = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferencesLogin.edit().putString(KEY_LOGIN, login).apply()
    }

    fun getLogin(context: Context): String? {
        val sharedLogin = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedLogin.getString(KEY_LOGIN, null)
    }
}
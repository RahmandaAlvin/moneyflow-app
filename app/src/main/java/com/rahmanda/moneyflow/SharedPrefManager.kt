package com.rahmanda.moneyflow.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("money_flow_pref", Context.MODE_PRIVATE)

    private val KEY_USERNAME = "username"
    fun saveUsername(username: String) {
        with(sharedPref.edit()) {
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun getUsername(): String? {
        return sharedPref.getString(KEY_USERNAME, null)
    }
    fun clearSession() {
        with(sharedPref.edit()) {
            remove(KEY_USERNAME)
            apply()
        }
    }
}
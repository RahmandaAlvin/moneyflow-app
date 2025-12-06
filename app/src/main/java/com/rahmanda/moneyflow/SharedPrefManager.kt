package com.rahmanda.moneyflow.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("money_flow_pref", Context.MODE_PRIVATE)

    private val KEY_USERNAME = "username"

    // Simpan Username setelah login
    fun saveUsername(username: String) {
        with(sharedPref.edit()) {
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    // Ambil Username untuk ditampilkan di Beranda
    fun getUsername(): String? {
        // Default ke null jika belum login
        return sharedPref.getString(KEY_USERNAME, null)
    }

    // Hapus sesi saat logout
    fun clearSession() {
        with(sharedPref.edit()) {
            remove(KEY_USERNAME)
            apply()
        }
    }
}
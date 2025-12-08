package com.rahmanda.moneyflow.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("money_flow_pref", Context.MODE_PRIVATE)

    //login
    private val KEY_USERNAME = "username"
    fun saveUsername(username: String) {
        with(sharedPref.edit()) {
            putString(KEY_USERNAME, username) // menerima usernem
            apply() // menyimpan
        }
    }
    // beranda
    fun getUsername(): String? { // fungsi yang digunakan untuk mengambil username untuk beranda
        return sharedPref.getString(KEY_USERNAME, null) // jika tidak ada username maka akan muncul null
    }
    // profil
    fun clearSession() { // fungsi yang di pangil di profil untuk logout
        with(sharedPref.edit()) { // membuka editor
            remove(KEY_USERNAME) // menghapus kunci key_usernem
            apply()  // menyimpan
        }
    }
}
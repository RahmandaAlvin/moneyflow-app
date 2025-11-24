package com.rahmanda.moneyflow.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("money_flow_pref", Context.MODE_PRIVATE)

    // Simpan data transaksi
    fun saveTransactionData(data: TransactionData) {
        with(sharedPref.edit()) {
            putInt("saldo", data.saldo)
            putInt("pemasukan", data.pemasukan)
            putInt("pengeluaran", data.pengeluaran)
            apply()
        }
    }

    // Ambil data transaksi
    fun getTransactionData(): TransactionData {
        return TransactionData(
            saldo = sharedPref.getInt("saldo", 45000),
            pemasukan = sharedPref.getInt("pemasukan", 100000),
            pengeluaran = sharedPref.getInt("pengeluaran", 65000)
        )
    }

    // Update saldo ketika ada transaksi baru
    fun updateSaldo(jumlah: Int, isPemasukan: Boolean) {
        val currentData = getTransactionData()

        if (isPemasukan) {
            currentData.saldo += jumlah
            currentData.pemasukan += jumlah
        } else {
            currentData.saldo -= jumlah
            currentData.pengeluaran += jumlah
        }

        saveTransactionData(currentData)
    }
}
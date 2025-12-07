package com.rahmanda.moneyflow.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rahmanda.moneyflow.Transaction
import com.rahmanda.moneyflow.TransactionGroup
import java.text.SimpleDateFormat
import java.util.Locale

object TransactionManager {
    // Nama file SharedPreferences untuk menyimpan data transaksi
    private const val PREF_NAME = "transaction_repo"

    // Key untuk menyimpan list transaksi dalam format JSON
    private const val KEY_TRANSACTIONS = "transaction_list"

    // Flag untuk mencegah inisialisasi berulang
    private var isInitialized = false

    // Gson instance untuk serialisasi/deserialisasi JSON
    private val gson = Gson()

    // In-memory storage untuk transaksi (di-load dari SharedPreferences)
    private val transactions: MutableList<Transaction> = mutableListOf()

    fun initialize(context: Context) {
        if (isInitialized) return  // Hindari load berulang
        loadTransactions(context)
        isInitialized = true
    }

    private fun loadTransactions(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(KEY_TRANSACTIONS, "[]")  // Default empty array

        // Clear existing data sebelum load baru
        transactions.clear()

        // Deserialize JSON ke List<Transaction>
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        transactions.addAll(gson.fromJson(json, type))
    }

    private fun saveTransactions(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(transactions)  // Serialize ke JSON

        with(sharedPref.edit()) {
            putString(KEY_TRANSACTIONS, json)
            apply()  // Async save
        }
    }

    fun addTransaction(context: Context, transaction: Transaction) {
        transactions.add(0, transaction)  // Add di awal list
        saveTransactions(context)          // Simpan perubahan
    }

    fun getAllTransactions(): List<Transaction> {
        return transactions.toList()  // Return copy untuk safety
    }

    fun getTotals(): Triple<Double, Double, Double> {
        var totalSaldo = 0.0
        var totalPemasukan = 0.0
        var totalPengeluaran = 0.0

        transactions.forEach { transaction ->
            if (transaction.type == "Pemasukan") {
                totalSaldo += transaction.amount
                totalPemasukan += transaction.amount
            } else {
                totalSaldo -= transaction.amount
                totalPengeluaran += transaction.amount
            }
        }

        // Triple: (Saldo, TotalPemasukan, TotalPengeluaran)
        return Triple(totalSaldo, totalPemasukan, totalPengeluaran)
    }

    fun getGroupedTransactions(): List<TransactionGroup> {
        // Format tanggal untuk grouping: "13 November 2025"
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        // 1. Group by tanggal yang sudah diformat
        // 2. Convert ke List<TransactionGroup>
        val groupedMap = transactions
            .groupBy { dateFormat.format(it.date) }
            .map { (date, list) -> TransactionGroup(date, list) }

        return groupedMap
    }
}
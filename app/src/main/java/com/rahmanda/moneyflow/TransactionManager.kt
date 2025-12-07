package com.rahmanda.moneyflow.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rahmanda.moneyflow.Transaction
import com.rahmanda.moneyflow.TransactionGroup
import java.util.Date
import java.util.Locale

object TransactionManager {
    private const val PREF_NAME = "transaction_repo"
    private const val KEY_TRANSACTIONS = "transaction_list"
    private var isInitialized = false
    private val gson = Gson()
    private val transactions: MutableList<Transaction> = mutableListOf()

    fun initialize(context: Context) {
        if (isInitialized) return
        loadTransactions(context)
        isInitialized = true
    }
    private fun loadTransactions(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(KEY_TRANSACTIONS, "[]")
        transactions.clear()
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        transactions.addAll(gson.fromJson(json, type))
    }
    private fun saveTransactions(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(transactions)
        with(sharedPref.edit()) {
            putString(KEY_TRANSACTIONS, json)
            apply()
        }
    }
    fun addTransaction(context: Context, transaction: Transaction) {
        transactions.add(0, transaction)
        saveTransactions(context)
    }
    fun getAllTransactions(): List<Transaction> {
        return transactions.toList()
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
        return Triple(totalSaldo, totalPemasukan, totalPengeluaran)
    }
    fun getGroupedTransactions(): List<TransactionGroup> {
        val dateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val groupedMap = transactions
            .groupBy { dateFormat.format(it.date) }
            .map { (date, list) -> TransactionGroup(date, list) }
        return groupedMap
    }
}
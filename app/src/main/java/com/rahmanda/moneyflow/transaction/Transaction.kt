package com.rahmanda.moneyflow

data class Transaction(
    val id: Int,
    val date: String, // Format: "13 November 2025"
    val type: String, // "Pemasukan" atau "Pengeluaran"
    val category: String, // Contoh: "Sertifikat Peny"
    val description: String, // Contoh: "RPA v 1.4"
    val amount: Double
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)
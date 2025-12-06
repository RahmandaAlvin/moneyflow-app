package com.rahmanda.moneyflow

import java.util.Date

// Model data untuk menyimpan setiap transaksi
data class Transaction(
    val id: Int,
    val date: Date, // Menggunakan Date object untuk sorting dan grouping yang akurat
    val type: String, // "Pemasukan" atau "Pengeluaran"
    val category: String, // Contoh: "Sertifikat Peny"
    // val description: String, // Contoh: "RPA v 1.4"
    val amount: Double
)

data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)
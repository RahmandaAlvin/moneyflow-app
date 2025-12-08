package com.rahmanda.moneyflow

import java.util.Date

data class Transaction(
    // ID unik untuk setiap transaksi
    val id: Int,

    val date: Date,

    val type: String,

    val category: String,

    val amount: Double
)

data class TransactionGroup(

    val date: String,

    val transactions: List<Transaction>
)
package com.rahmanda.moneyflow

import java.util.Date

data class Transaction(
    val id: Int,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Date,
    val time: String
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
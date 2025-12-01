package com.rahmanda.moneyflow.transaction

data class Transaction(
    val id: Int = 0,
    val type: String, // "PEMASUKAN" atau "PENGELUARAN"
    val amount: Long,
    val description: String,
    val date: String,
    var saldo: Int = 45000,
    var pemasukan: Int = 100000,
    var pengeluaran:Int = 65000

)
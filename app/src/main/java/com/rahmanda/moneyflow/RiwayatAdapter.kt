package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RiwayatAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    private val dateFormatDisplay = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set data transaksi
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        // Format tanggal dan waktu
        val dateTime = "${dateFormatDisplay.format(transaction.date)} ${transaction.time}"
        holder.tvDate.text = dateTime

        // Format jumlah dengan warna berbeda untuk pemasukan/pengeluaran
        val amountText = formatCurrency(transaction.amount)

        if (transaction.type == TransactionType.INCOME) {
            holder.tvAmount.text = "+ $amountText"
            holder.tvAmount.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
        } else {
            holder.tvAmount.text = "- $amountText"
            holder.tvAmount.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    private fun formatCurrency(amount: Double): String {
        return "Rp ${String.format(Locale("id", "ID"), "%,.0f", amount).replace(",", ".")}"
    }
}
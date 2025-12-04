package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rahmanda.moneyflow.R

class RiwayatAdapter(private val transactionGroups: List<TransactionGroup>) :
    RecyclerView.Adapter<RiwayatAdapter.DateGroupViewHolder>() {

    class DateGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val containerItems: ViewGroup = itemView.findViewById(R.id.containerItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_with_date, parent, false)
        return DateGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateGroupViewHolder, position: Int) {
        val group = transactionGroups[position]

        // Set tanggal
        holder.tvDate.text = group.date

        // Clear container
        holder.containerItems.removeAllViews()

        // Tambahkan item transaksi ke container
        group.transactions.forEach { transaction ->
            val itemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_transaction, holder.containerItems, false)

            bindTransactionItem(itemView, transaction)
            holder.containerItems.addView(itemView)
        }
    }

    private fun bindTransactionItem(itemView: View, transaction: Transaction) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)

        // Set data
        tvCategory.text = transaction.category
        tvDescription.text = transaction.type

        // Format tanggal untuk tvDateTime (ambil hari dan bulan saja)
        // Contoh: "13 November 2025" -> "13 NOV"
        val dateParts = transaction.date.split(" ")
        if (dateParts.size >= 2) {
            val day = dateParts[0]
            val month = dateParts[1].take(3).toUpperCase() // Ambil 3 huruf pertama
            tvDateTime.text = "$day $month"
        } else {
            tvDateTime.text = transaction.date
        }

        // Set icon dan warna berdasarkan jenis transaksi
        if (transaction.type == "Pemasukan") {
            // Untuk pemasukan
            ivIcon.setImageResource(R.drawable.increase)
            ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)
            tvAmount.text = "+ Rp ${transaction.amount.toInt()}"
            tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            // Untuk pengeluaran
            ivIcon.setImageResource(R.drawable.decrease)
            ivIcon.setBackgroundResource(R.drawable.icon_circle_red)
            tvAmount.text = "- Rp ${transaction.amount.toInt()}"
            tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
        }
    }

    override fun getItemCount(): Int = transactionGroups.size
}
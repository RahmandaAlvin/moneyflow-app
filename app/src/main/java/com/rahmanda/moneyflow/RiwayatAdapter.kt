package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rahmanda.moneyflow.R
import java.text.SimpleDateFormat
import java.util.Locale

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

        // Set tanggal (sudah diformat di TransactionManager)
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
        val ivIcon: ImageView = itemView.findViewById(R.id.tvIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)

        // Set data
        tvCategory.text = transaction.category
        tvDescription.text = transaction.type

        // --- PERBAIKAN: HANYA TAMPILKAN JAM DAN MENIT (HH:mm) ---
        // Pemformatan objek Date untuk menghasilkan JAM:MENIT
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val formattedTime = timeFormat.format(transaction.date)

        tvDateTime.text = formattedTime
        // --- END PERBAIKAN ---


        // Set icon dan warna berdasarkan jenis transaksi
        if (transaction.type == "Pemasukan") {
            // Untuk pemasukan
            ivIcon.setImageResource(R.drawable.increase)
            ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)
            tvAmount.text = "+ Rp ${transaction.amount.toInt()}"
            tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_blue_dark))
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
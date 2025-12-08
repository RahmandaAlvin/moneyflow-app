package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Locale

class RiwayatAdapter(private val transactionGroups: List<TransactionGroup>) :
    RecyclerView.Adapter<RiwayatAdapter.DateGroupViewHolder>() {

    class DateGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder untuk item_transaction_with_date
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

        holder.tvDate.text = group.date
        holder.containerItems.removeAllViews()

        group.transactions.forEach { transaction ->
            val itemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_transaction, holder.containerItems, false)

            // Memanggil bindTransactionItem dengan holder yang benar
            bindTransactionItem(itemView, holder, transaction)
            holder.containerItems.addView(itemView)
        }
    }

    // Mengubah fungsi ini agar menerima holder untuk akses context
    private fun bindTransactionItem(itemView: View, holder: DateGroupViewHolder, transaction: Transaction) {
        // Mengakses View di dalam item_transaction.xml
        val ivIcon: ImageView = itemView.findViewById(R.id.tvIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)

        // Pemformatan Nominal Rupiah
        val rupiahFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = rupiahFormat.format(transaction.amount.toLong())

        tvCategory.text = transaction.category
        tvDescription.text = transaction.type

        // Pemformatan Waktu: HANYA JAM DAN MENIT (HH:mm)
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val formattedTime = timeFormat.format(transaction.date)

        tvDateTime.text = formattedTime

        // Logika Warna dan Ikon
        if (transaction.type == "Pemasukan") {
            // PEMASUKAN: Icon biru, text hijau
            ivIcon.setImageResource(R.drawable.increase)
            ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)
            tvAmount.text = "+ Rp $formattedAmount"
            tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.blue_dark)
            )
        } else {
            // PENGELUARAN: Icon merah, text merah
            ivIcon.setImageResource(R.drawable.decrease)
            ivIcon.setBackgroundResource(R.drawable.icon_circle_red)
            tvAmount.text = "- Rp $formattedAmount"
            tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        }
    }

    override fun getItemCount(): Int = transactionGroups.size
}
package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class BerandaAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<BerandaAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        // HAPUS INI: val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        // Karena tvTime sudah dihapus dari XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_beranda, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // 1. Set kategori dan tipe transaksi
        holder.tvCategory.text = transaction.category
        holder.tvType.text = transaction.type

        // 2. Format nominal ke Rupiah
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = format.format(transaction.amount.toInt())

        // 3. Set tampilan berdasarkan jenis transaksi
        if (transaction.type == "Pemasukan") {
            // PEMASUKAN: Icon biru, text hijau
            holder.ivIcon.setImageResource(R.drawable.increase)
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)
            holder.tvAmount.text = "+ Rp $formattedAmount"
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.blue_dark)
            )
        } else {
            // PENGELUARAN: Icon merah, text merah
            holder.ivIcon.setImageResource(R.drawable.decrease)
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_red)
            holder.tvAmount.text = "- Rp $formattedAmount"
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        }

        // 4. TIDAK PERLU ADA KODE TVTIME LAGI
        // Karena tvTime sudah dihapus dari XML dan ViewHolder
    }

    override fun getItemCount(): Int = transactions.size
}
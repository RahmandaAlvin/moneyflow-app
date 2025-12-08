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

// Adapter untuk RecyclerView di halaman Beranda
class BerandaAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<BerandaAdapter.TransactionViewHolder>() {

    // ViewHolder: menyimpan referensi ke elemen UI di setiap item
    // Menggunakan layout: item_transaction_beranda.xml
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

        val tvType: TextView = itemView.findViewById(R.id.tvType)

        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

    }

    // Membuat view dari layout XML dan mengembalikan ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_beranda, parent, false)
        return TransactionViewHolder(view)
    }

    // Mengatur tampilan setiap item transaksi
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Ambil data transaksi berdasarkan posisi
        val transaction = transactions[position]

        // Tampilkan kategori dan jenis transaksi
        holder.tvCategory.text = transaction.category
        holder.tvType.text = transaction.type

        // Format nominal ke format Rupiah Indonesia
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = format.format(transaction.amount.toInt())

        // Tentukan tampilan berdasarkan jenis transaksi
        if (transaction.type == "Pemasukan") {
            holder.ivIcon.setImageResource(R.drawable.increase)
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)
            holder.tvAmount.text = "+ Rp $formattedAmount"
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.blue_dark)
            )
        } else {
            holder.ivIcon.setImageResource(R.drawable.decrease)
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_red)
            holder.tvAmount.text = "- Rp $formattedAmount"
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        }

    }

    // Mengembalikan jumlah item dalam dataset
    override fun getItemCount(): Int = transactions.size
}
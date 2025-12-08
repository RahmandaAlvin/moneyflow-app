package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionItemAdapter(private val transactions: List<Transaction>) : // membuat kelas yang bernama TransactionItemAdapter
    RecyclerView.Adapter<TransactionItemAdapter.TransactionViewHolder>() { // menggunakan RecyclerView untuk mengelola mengelola daftar

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //membuat class yang bernama TransactionViewHolder
        val tvType: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount) // menggunakan findViewById untuk menyimpan text view dalam file
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder { // membuat fungsi untuk membuat view baru untuk baris per item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false) // digunakan untuk memanggil file item_transaction dari layout
        return TransactionViewHolder(view) // digunakn untuk mengembalikan view holder baru yang berisi view
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) { // mmebuat fungsi onBindViewHolder untuk mengisi holder view
        val transaction = transactions[position] // mengambil data transactions

        holder.tvType.text = transaction.type //
        holder.tvAmount.text = "Rp ${transaction.amount.toInt()}" // mengisi tvAmount dengan nominal transaksi, diformat menjadi rupiah

        if (transaction.type == "Pemasukan") {
            holder.tvType.setTextColor(holder.itemView.context.getColor(R.color.blue))
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(R.color.blue))
        } else {
            holder.tvType.setTextColor(holder.itemView.context.getColor(R.color.red))
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(R.color.red))
        }
        // jika uang masuk nanti berwarna biru dan uang keluar nanti berwarna merah.

        if (transaction.category.isNotEmpty()) {
            holder.tvCategory.text = transaction.category
            holder.tvCategory.visibility = View.VISIBLE
        } else {
            holder.tvCategory.visibility = View.GONE
        }
    } // jika ada kategori,teks diisi dan view di tampilkan jika tidak view di sembunyikan

    override fun getItemCount(): Int = transactions.size // membuat fungsi untuk memberitahu RecyclerView berapa banyak total item yang terdaftar
}
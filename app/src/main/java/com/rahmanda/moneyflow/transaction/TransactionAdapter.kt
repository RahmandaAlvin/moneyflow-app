package com.rahmanda.moneyflow.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.transaction.Transaction

// DEFINISI KELAS UTAMA:
class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    // KELAS BERSARANG (NESTED CLASS) HARUS BERADA DI SINI, BUKAN DEFINISI ADAPTER LAGI
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // UBAH DARI ID LAMA KE ID YANG BENAR DARI XML
        private val tvCategory: TextView =
            itemView.findViewById(R.id.tvCategory)
        private val tvDescription: TextView =
            itemView.findViewById(R.id.tvDescription)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView =
            itemView.findViewById(R.id.tvAmount)

        fun bind(transaction: Transaction) {
            // Perbarui semua penggunaan variabel di sini
            tvCategory.text = transaction.type
            tvDescription.text = transaction.description
            tvDate.text = transaction.date

            // Set warna berdasarkan jenis transaksi (gunakan tvAmount dan tvCategory yang baru)
            if (transaction.type == "PEMASUKAN") {
                tvAmount.text = "+ Rp ${transaction.amount}"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                tvCategory.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                tvAmount.text = "- Rp ${transaction.amount}"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                tvCategory.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Perlu properti 'id' di data class Transaction agar ini berfungsi
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} // <-- KURUNG KURAWAL PENUTUP UNTUK KELAS UTAMA
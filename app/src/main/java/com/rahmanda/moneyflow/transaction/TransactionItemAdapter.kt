package com.rahmanda.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionItemAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionItemAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvType.text = transaction.type
    //    holder.tvDescription.text = transaction.description
        holder.tvAmount.text = "Rp ${transaction.amount.toInt()}"

        // Set warna berdasarkan jenis
        if (transaction.type == "Pemasukan") {
            holder.tvType.setTextColor(holder.itemView.context.getColor(R.color.green))
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(R.color.green))
        } else {
            holder.tvType.setTextColor(holder.itemView.context.getColor(R.color.red))
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(R.color.red))
        }

        // Category
        if (transaction.category.isNotEmpty()) {
            holder.tvCategory.text = transaction.category
            holder.tvCategory.visibility = View.VISIBLE
        } else {
            holder.tvCategory.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = transactions.size
}
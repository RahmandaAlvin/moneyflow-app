//package com.rahmanda.moneyflow.transaction
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import java.text.SimpleDateFormat
//import java.util.*
//
//// Definisikan class Transaction di sini
//data class Transaction(
//    val id: Int,
//    val type: TransactionType,
//    val amount: Double,
//    val description: String,
//    val category: String,
//    val date: Date,
//    val time: String
//)
//
//enum class TransactionType {
//    INCOME,
//    EXPENSE
//}
//
//class TransactionAdapter(private var transactions: List<Transaction>) :
//    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
//        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
//        val tvDate: TextView = view.findViewById(R.id.tvDate)
//        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaction, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val transaction = transactions[position]
//        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
//
//        holder.tvCategory.text = transaction.category
//        holder.tvDescription.text = transaction.description
//        holder.tvDate.text = "${dateFormat.format(transaction.date)}"
//
//        val amountText = formatCurrency(transaction.amount)
//
//        if (transaction.type == TransactionType.INCOME) {
//            holder.tvAmount.text = "+ $amountText"
//            holder.tvAmount.setTextColor(
//                holder.itemView.context.getColor(android.R.color.holo_green_dark)
//            )
//        } else {
//            holder.tvAmount.text = "- $amountText"
//            holder.tvAmount.setTextColor(
//                holder.itemView.context.getColor(android.R.color.holo_red_dark)
//            )
//        }
//    }
//
//    override fun getItemCount(): Int = transactions.size
//
//    fun updateData(newTransactions: List<Transaction>) {
//        transactions = newTransactions
//        notifyDataSetChanged()
//    }
//
//    private fun formatCurrency(amount: Double): String {
//        return "Rp ${String.format(Locale("id", "ID"), "%,.0f", amount).replace(",", ".")}"
//    }
//}
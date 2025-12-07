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
// Menampilkan maksimal 4 transaksi terbaru tanpa informasi tanggal/waktu
class BerandaAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<BerandaAdapter.TransactionViewHolder>() {

    // ViewHolder: menyimpan referensi ke elemen UI di setiap item
    // Menggunakan layout: item_transaction_beranda.xml
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Icon lingkaran (biru untuk pemasukan, merah untuk pengeluaran)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        // Nama kategori transaksi (contoh: "Beli Nasi Padang")
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

        // Jenis transaksi ("Pemasukan" atau "Pengeluaran")
        val tvType: TextView = itemView.findViewById(R.id.tvType)

        // Jumlah nominal transaksi dengan format Rupiah
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

    }

    // Dipanggil ketika RecyclerView membutuhkan ViewHolder baru
    // Membuat view dari layout XML dan mengembalikan ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_beranda, parent, false)
        return TransactionViewHolder(view)
    }

    // Dipanggil untuk mengikat data ke ViewHolder pada posisi tertentu
    // Inilah tempat kita mengatur tampilan setiap item transaksi
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Ambil data transaksi berdasarkan posisi
        val transaction = transactions[position]

        // Tampilkan kategori dan jenis transaksi
        holder.tvCategory.text = transaction.category
        holder.tvType.text = transaction.type

        // Format nominal ke format Rupiah Indonesia (contoh: 20.000)
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = format.format(transaction.amount.toInt())

        // Tentukan tampilan berdasarkan jenis transaksi
        if (transaction.type == "Pemasukan") {
            // Transaksi pemasukan: icon biru, teks hijau, tanda plus
            holder.ivIcon.setImageResource(R.drawable.increase)  // Icon panah naik
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_blue)  // Lingkaran biru
            holder.tvAmount.text = "+ Rp $formattedAmount"  // Tanda plus
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)  // Warna hijau
            )
        } else {
            // Transaksi pengeluaran: icon merah, teks merah, tanda minus
            holder.ivIcon.setImageResource(R.drawable.decrease)  // Icon panah turun
            holder.ivIcon.setBackgroundResource(R.drawable.icon_circle_red)  // Lingkaran merah
            holder.tvAmount.text = "- Rp $formattedAmount"  // Tanda minus
            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)  // Warna merah
            )
        }

    }

    // Mengembalikan jumlah item dalam dataset
    override fun getItemCount(): Int = transactions.size
}
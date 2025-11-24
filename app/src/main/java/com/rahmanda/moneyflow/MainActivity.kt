package com.rahmanda.moneyflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.data.SharedPrefManager
import com.rahmanda.moneyflow.data.TransactionData
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textWelcome: TextView
    private lateinit var textDate: TextView
    private lateinit var textSaldo: TextView
    private lateinit var textPemasukan: TextView
    private lateinit var textPengeluaran: TextView
    private lateinit var textLihatSemua: TextView
    private lateinit var iconMata: ImageView
    private lateinit var cardPemasukan: View
    private lateinit var cardPengeluaran: View

    private var isSaldoVisible = true
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefManager = SharedPrefManager(this)
        initViews()
        setupFunctions()
    }

    override fun onResume() {
        super.onResume()
        // Update data setiap kali kembali ke halaman ini
        updateDisplayData()
    }

    private fun initViews() {
        textWelcome = findViewById(R.id.textWelcome)
        textDate = findViewById(R.id.textDate)
        textSaldo = findViewById(R.id.textSaldo)
        textPemasukan = findViewById(R.id.textPemasukan)
        textPengeluaran = findViewById(R.id.textPengeluaran)
        textLihatSemua = findViewById(R.id.textLihatSemua)
        iconMata = findViewById(R.id.iconMata)
        cardPemasukan = findViewById(R.id.cardPemasukan)
        cardPengeluaran = findViewById(R.id.cardPengeluaran)
    }

    private fun setupFunctions() {
        setupCurrentDate()
        setupSaldoToggle()
        setupClickListeners()
        updateDisplayData() // Tampilkan data pertama kali
    }

    private fun setupCurrentDate() {
        val currentDate = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            .format(Date())
        textDate.text = currentDate
    }

    private fun setupSaldoToggle() {
        iconMata.setOnClickListener {
            isSaldoVisible = !isSaldoVisible
            updateSaldoDisplay()
        }
    }

    private fun setupClickListeners() {
        // 1. Lihat Semua Transaksi
        textLihatSemua.setOnClickListener {
            // Ke RiwayatActivity (ganti dengan class yang sesuai)
            try {
                val intent = Intent(this, Class.forName("com.rahmanda.moneyflow.RiwayatActivity"))
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Halaman Riwayat belum tersedia", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Tambah Pemasukan
        cardPemasukan.setOnClickListener {
            // Ke InputActivity dengan tipe pemasukan
            try {
                val intent = Intent(this, Class.forName("com.rahmanda.moneyflow.InputActivity"))
                intent.putExtra("TRANSACTION_TYPE", "pemasukan")
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Buat Pemasukan - Halaman Input belum tersedia", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Tambah Pengeluaran
        cardPengeluaran.setOnClickListener {
            // Ke InputActivity dengan tipe pengeluaran
            try {
                val intent = Intent(this, Class.forName("com.rahmanda.moneyflow.InputActivity"))
                intent.putExtra("TRANSACTION_TYPE", "pengeluaran")
                startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Buat Pengeluaran - Halaman Input belum tersedia", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDisplayData() {
        val data = sharedPrefManager.getTransactionData()
        updateData(data.saldo, data.pemasukan, data.pengeluaran)
    }

    private fun updateData(saldo: Int, pemasukan: Int, pengeluaran: Int) {
        // Format angka ke Rupiah
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textPemasukan.text = "Rp ${format.format(pemasukan)}"
        textPengeluaran.text = "Rp ${format.format(pengeluaran)}"
        updateSaldoDisplay(saldo)
    }

    private fun updateSaldoDisplay(saldo: Int? = null) {
        val currentSaldo = saldo ?: sharedPrefManager.getTransactionData().saldo
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textSaldo.text = if (isSaldoVisible) {
            "Rp ${format.format(currentSaldo)}"
        } else {
            "Rp ••••••"
        }
    }

    // Fungsi untuk diakses dari activity lain
    fun updateFromInput(jumlah: Int, isPemasukan: Boolean) {
        sharedPrefManager.updateSaldo(jumlah, isPemasukan)
        updateDisplayData()
    }
}
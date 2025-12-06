package com.rahmanda.moneyflow.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.data.SharedPrefManager
import com.rahmanda.moneyflow.data.TransactionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BerandaFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        sharedPrefManager = SharedPrefManager(requireContext())

        // PENTING: Inisialisasi TransactionManager jika belum
        TransactionManager.initialize(requireContext())

        initViews(view)
        setupFunctions()
        return view
    }

    override fun onResume() {
        super.onResume()
        // Update data setiap kali kembali ke halaman ini
        updateDisplayData()
    }

    private fun initViews(view: View) {
        textWelcome = view.findViewById(R.id.textWelcome)
        textDate = view.findViewById(R.id.textDate)
        textSaldo = view.findViewById(R.id.textSaldo)
        textPemasukan = view.findViewById(R.id.textPemasukan)
        textPengeluaran = view.findViewById(R.id.textPengeluaran)
        textLihatSemua = view.findViewById(R.id.textLihatSemua)
        iconMata = view.findViewById(R.id.iconMata)
        cardPemasukan = view.findViewById(R.id.cardPemasukan)
        cardPengeluaran = view.findViewById(R.id.cardPengeluaran)
    }

    private fun setupFunctions() {
        setupCurrentDate()
        setupWelcomeText() // Fungsi baru untuk set username
        setupSaldoToggle()
        setupClickListeners()
        // updateDisplayData() dipanggil di onResume
    }

    private fun setupWelcomeText() {
        val username = sharedPrefManager.getUsername() ?: "Pengguna" // Ambil username yang login
        textWelcome.text = "Selamat Datang, $username"
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
        // 1. Lihat Semua Transaksi -> Navigasi ke RiwayatFragment (sudah benar)
        textLihatSemua.setOnClickListener {
            // Kita asumsikan ini berjalan di Navigation Component
            findNavController().navigate(R.id.riwayatFragment)
        }

        // PENTING: Menghapus navigasi ke InputFragment dari Beranda
        // Karena InputFragment (InputActivity) diakses dari Tombol Tengah di HomeActivity
        // Jika Anda ingin tetap mempertahankan navigasi ini, biarkan saja
        // Jika tidak, Anda bisa menghapus cardPemasukan dan cardPengeluaran click listener.

        /* cardPemasukan.setOnClickListener {
             // Logic untuk ke InputActivity
         }

         cardPengeluaran.setOnClickListener {
             // Logic untuk ke InputActivity
         } */
    }

    private fun updateDisplayData() {
        // Ambil data total dari TransactionManager
        val (saldo, pemasukan, pengeluaran) = TransactionManager.getTotals()
        updateData(saldo, pemasukan, pengeluaran)
    }

    private fun updateData(saldo: Double, pemasukan: Double, pengeluaran: Double) {
        // Format angka ke Rupiah (hanya tampilkan integer)
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textPemasukan.text = "Rp ${format.format(pemasukan.toInt())}"
        textPengeluaran.text = "Rp ${format.format(pengeluaran.toInt())}"
        updateSaldoDisplay(saldo)

        // HIDE TRANSAKSI TERAKHIR JIKA KOSONG (Logic ini biasanya di RecyclerView)
        // Karena Transaksi Terakhir di layout Anda adalah hardcoded, kita abaikan dulu
        // atau kita set visibility ke GONE jika TransactionManager.getAllTransactions().isEmpty()
    }

    private fun updateSaldoDisplay(saldo: Double? = null) {
        val currentSaldo = saldo ?: TransactionManager.getTotals().first
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textSaldo.text = if (isSaldoVisible) {
            "Rp ${format.format(currentSaldo.toLong())}"
        } else {
            "Rp ••••••"
        }
    }

    // updateFromInput tidak lagi diperlukan di sini karena data diakses melalui TransactionManager
}
package com.rahmanda.moneyflow.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahmanda.moneyflow.BerandaAdapter
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.data.SharedPrefManager
import com.rahmanda.moneyflow.data.TransactionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BerandaFragment : Fragment() {

    // Deklarasi variabel untuk komponen UI
    private lateinit var textWelcome: TextView
    private lateinit var textDate: TextView
    private lateinit var textSaldo: TextView
    private lateinit var textPemasukan: TextView
    private lateinit var textPengeluaran: TextView
    private lateinit var textLihatSemua: TextView
    private lateinit var iconMata: ImageView
    private lateinit var cardPemasukan: View
    private lateinit var cardPengeluaran: View

    // Variabel untuk RecyclerView transaksi terakhir
    private lateinit var recyclerViewTransaksi: RecyclerView
    private lateinit var berandaAdapter: BerandaAdapter

    private var isSaldoVisible = true

    // Manager untuk menyimpan dan mengambil data pengguna
    private lateinit var sharedPrefManager: SharedPrefManager

    // Fungsi utama yang dipanggil saat fragment dibuat
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout dari XML ke View
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Inisialisasi SharedPrefManager untuk mengelola session pengguna
        sharedPrefManager = SharedPrefManager(requireContext())

        // TransactionManager sudah siap digunakan
        TransactionManager.initialize(requireContext())

        // Setup semua komponen dan fungsi
        initViews(view)
        setupFunctions()
        return view
    }

    // Dipanggil setiap kali fragment kembali tampil
    override fun onResume() {
        super.onResume()
        updateDisplayData()
        loadLatestTransactions()
    }

    // Inisialisasi semua komponen UI dari layout
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

        // Inisialisasi RecyclerView untuk daftar transaksi
        recyclerViewTransaksi = view.findViewById(R.id.recyclerViewTransaksiTerakhir)
    }

    // Setup semua fungsi yang dibutuhkan
    private fun setupFunctions() {
        setupCurrentDate()
        setupWelcomeText()
        setupSaldoToggle()
        setupClickListeners()
        setupRecyclerView()
        updateDisplayData()
    }

    // Menampilkan username pengguna yang sedang login
    private fun setupWelcomeText() {
        val username = sharedPrefManager.getUsername() ?: "Pengguna"
        textWelcome.text = "Selamat Datang, $username"
    }

    // Menampilkan bulan dan tahun saat ini
    private fun setupCurrentDate() {
        val currentDate = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            .format(Date())
        textDate.text = currentDate
    }

    // Fungsi untuk toggle show/hide saldo ketika icon mata diklik
    private fun setupSaldoToggle() {
        iconMata.setOnClickListener {
            isSaldoVisible = !isSaldoVisible
            updateSaldoDisplay()
        }
    }

    // Setup RecyclerView: layout manager
    private fun setupRecyclerView() {
        recyclerViewTransaksi.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewTransaksi.isNestedScrollingEnabled = false
        loadLatestTransactions()
    }

    // Mengambil 4 transaksi terbaru dari TransactionManager
    private fun loadLatestTransactions() {
        val latestTransactions = TransactionManager.getAllTransactions()
            .take(4)

        // Setup adapter dengan data transaksi
        berandaAdapter = BerandaAdapter(latestTransactions)
        recyclerViewTransaksi.adapter = berandaAdapter

        // Sembunyikan RecyclerView jika tidak ada transaksi
        if (latestTransactions.isEmpty()) {
            recyclerViewTransaksi.visibility = View.GONE
        } else {
            recyclerViewTransaksi.visibility = View.VISIBLE
        }
    }

    // Setup semua event klik di halaman
    private fun setupClickListeners() {
        textLihatSemua.setOnClickListener {
            findNavController().navigate(R.id.riwayatFragment)
        }

    }

    // Mengambil dan menampilkan data saldo, pemasukan, pengeluaran
    private fun updateDisplayData() {
        val (saldo, pemasukan, pengeluaran) = TransactionManager.getTotals()
        updateData(saldo, pemasukan, pengeluaran)
    }

    // Menampilkan data ke UI dengan format Rupiah
    private fun updateData(saldo: Double, pemasukan: Double, pengeluaran: Double) {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textPemasukan.text = "Rp ${format.format(pemasukan.toInt())}"
        textPengeluaran.text = "Rp ${format.format(pengeluaran.toInt())}"
        updateSaldoDisplay(saldo)
    }

    // Menampilkan atau menyembunyikan saldo berdasarkan status isSaldoVisible
    private fun updateSaldoDisplay(saldo: Double? = null) {
        val currentSaldo = saldo ?: TransactionManager.getTotals().first
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textSaldo.text = if (isSaldoVisible) {
            "Rp ${format.format(currentSaldo.toLong())}"
        } else {
            "Rp ••••••"
        }
    }
}
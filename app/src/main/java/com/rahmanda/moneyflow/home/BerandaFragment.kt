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

    private lateinit var textWelcome: TextView
    private lateinit var textDate: TextView
    private lateinit var textSaldo: TextView
    private lateinit var textPemasukan: TextView
    private lateinit var textPengeluaran: TextView
    private lateinit var textLihatSemua: TextView
    private lateinit var iconMata: ImageView
    private lateinit var cardPemasukan: View
    private lateinit var cardPengeluaran: View

    // TAMBAHAN: RecyclerView untuk transaksi terakhir
    private lateinit var recyclerViewTransaksi: RecyclerView
    private lateinit var berandaAdapter: BerandaAdapter

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
        loadLatestTransactions() // TAMBAHAN: Refresh transaksi terakhir
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

        // TAMBAHAN: Inisialisasi RecyclerView
        recyclerViewTransaksi = view.findViewById(R.id.recyclerViewTransaksiTerakhir)
    }

    private fun setupFunctions() {
        setupCurrentDate()
        setupWelcomeText()
        setupSaldoToggle()
        setupClickListeners()
        setupRecyclerView() // TAMBAHAN: Setup RecyclerView
        updateDisplayData()
    }

    private fun setupWelcomeText() {
        val username = sharedPrefManager.getUsername() ?: "Pengguna"
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

    // TAMBAHAN: Setup RecyclerView untuk transaksi terakhir
    private fun setupRecyclerView() {
        // Setup layout manager
        recyclerViewTransaksi.layoutManager = LinearLayoutManager(requireContext())

        // Nonaktifkan scroll nested (biar scroll parent ScrollView yang bekerja)
        recyclerViewTransaksi.isNestedScrollingEnabled = false

        // Load transaksi terbaru
        loadLatestTransactions()
    }

    // TAMBAHAN: Load 4 transaksi terbaru
    private fun loadLatestTransactions() {
        val latestTransactions = TransactionManager.getAllTransactions()
            .take(4) // Ambil maksimal 4 transaksi terbaru

        // Update adapter
        berandaAdapter = BerandaAdapter(latestTransactions)
        recyclerViewTransaksi.adapter = berandaAdapter

        // Jika tidak ada transaksi, sembunyikan RecyclerView (optional)
        if (latestTransactions.isEmpty()) {
            recyclerViewTransaksi.visibility = View.GONE
        } else {
            recyclerViewTransaksi.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        // 1. Lihat Semua Transaksi -> Navigasi ke RiwayatFragment
        textLihatSemua.setOnClickListener {
            findNavController().navigate(R.id.riwayatFragment)
        }

        // Hapus click listener untuk cardPemasukan & cardPengeluaran
        // karena input transaksi via Bottom Navigation
    }

    private fun updateDisplayData() {
        // Ambil data total dari TransactionManager
        val (saldo, pemasukan, pengeluaran) = TransactionManager.getTotals()
        updateData(saldo, pemasukan, pengeluaran)
    }

    private fun updateData(saldo: Double, pemasukan: Double, pengeluaran: Double) {
        // Format angka ke Rupiah
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))

        textPemasukan.text = "Rp ${format.format(pemasukan.toInt())}"
        textPengeluaran.text = "Rp ${format.format(pengeluaran.toInt())}"
        updateSaldoDisplay(saldo)
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
}
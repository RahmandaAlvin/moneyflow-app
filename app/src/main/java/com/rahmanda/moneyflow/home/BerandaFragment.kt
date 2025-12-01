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
        // Inflate layout fragment_beranda yang sudah berisi layout lengkap
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        sharedPrefManager = SharedPrefManager(requireContext())
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
        // 1. Lihat Semua Transaksi -> Navigasi ke RiwayatFragment
        textLihatSemua.setOnClickListener {
            findNavController().navigate(R.id.riwayatFragment)
        }

        // 2. Card Pemasukan -> Navigasi ke InputFragment
        cardPemasukan.setOnClickListener {
            findNavController().navigate(R.id.inputFragment)
        }

        // 3. Card Pengeluaran -> Navigasi ke InputFragment
        cardPengeluaran.setOnClickListener {
            findNavController().navigate(R.id.inputFragment)
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

    fun updateFromInput(jumlah: Int, isPemasukan: Boolean) {
        sharedPrefManager.updateSaldo(jumlah, isPemasukan)
        updateDisplayData()
    }
}
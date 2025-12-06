package com.rahmanda.moneyflow.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.data.TransactionManager
import com.rahmanda.moneyflow.databinding.FragmentRiwayatBinding
import com.rahmanda.moneyflow.RiwayatAdapter
import com.rahmanda.moneyflow.TransactionGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RiwayatAdapter

    // Variabel untuk filter
    private var selectedDate: String = ""
    private var selectedType: String = "Semua"

    // Untuk date picker
    private val calendar = Calendar.getInstance()
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    // Data asli (diambil dari Manager, bukan mutableList lokal)
    private var allTransactionGroups = listOf<TransactionGroup>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)

        // PENTING: Inisialisasi TransactionManager jika belum
        TransactionManager.initialize(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Atur tampilan awal TextView
        binding.textViewTanggal.text = "Pilih tanggal"
        binding.textViewJenis.text = "Semua"

        setupFilters()
        setupRecyclerView()
        loadTransactionData() // Muat data pertama kali
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data setiap kali kembali ke Riwayat
        loadTransactionData()
    }

    // ... (setupFilters, setupJenisPicker, updateJenisIcon, setupTanggalPicker, openDatePicker)

    // ==================== SETUP FILTERS ====================
    private fun setupFilters() {
        setupJenisPicker() // Mengganti Spinner Jenis
        setupTanggalPicker() // Mengganti Spinner Tanggal
    }

    // 1. JENIS PICKER (MENGGANTIKAN SPINNER)
    private fun setupJenisPicker() {
        // Set icon awal
        updateJenisIcon(0)

        binding.textViewJenis.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.textViewJenis)
            val jenisList = listOf("Semua", "Pemasukan", "Pengeluaran")

            // Tambahkan item ke menu
            jenisList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            popup.setOnMenuItemClickListener { item ->
                selectedType = item.title.toString()
                binding.textViewJenis.text = selectedType // Update teks TextView

                // Cari index item yang dipilih untuk mengupdate ikon
                val position = jenisList.indexOf(selectedType)
                updateJenisIcon(position)
                applyFilters()
                true
            }
            popup.show()
        }
    }

    // UPDATE ICON JENIS
    private fun updateJenisIcon(position: Int) {
        when (position) {
            0 -> { // Semua
                binding.ivIconJenis.setImageResource(R.drawable.semua)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
            1 -> { // Pemasukan
                binding.ivIconJenis.setImageResource(R.drawable.increase)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
            2 -> { // Pengeluaran
                binding.ivIconJenis.setImageResource(R.drawable.decrease)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
        }
    }

    // 2. TANGGAL PICKER (MENGGANTIKAN SPINNER)
    private fun setupTanggalPicker() {
        // Set icon warna putih
        binding.ivIconTanggal.setColorFilter(Color.WHITE)

        binding.textViewTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Format tanggal (untuk filter)
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

                // Format untuk display
                val displayDate = displayFormat.format(
                    Calendar.getInstance().apply {
                        set(year, month, day)
                    }.time
                )

                // Update TextView
                binding.textViewTanggal.text = displayDate

                // Apply filter
                applyFilters()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setTitle("Pilih Tanggal")
        dialog.show()
    }

    // ==================== APPLY FILTERS ====================
    private fun applyFilters() {
        // Base data: selalu dari data yang dimuat (yang sudah dikelompokkan)
        var filteredGroups = allTransactionGroups

        // 1. Filter by date (dikerjakan di filterByDate)
        if (selectedDate.isNotEmpty() && selectedDate != "Pilih tanggal") {
            // Karena selectedDate masih string 'dd/MM/yyyy', kita harus mengkonversinya
            filteredGroups = filterByDate(selectedDate, filteredGroups)
        }

        // 2. Filter by type
        val finalFilteredGroups = if (selectedType != "Semua") {
            filterByType(selectedType, filteredGroups)
        } else {
            filteredGroups
        }

        // Update adapter
        adapter = RiwayatAdapter(finalFilteredGroups)
        binding.recyclerViewRiwayat.adapter = adapter
    }

    private fun filterByDate(dateStr: String, groups: List<TransactionGroup>): List<TransactionGroup> {
        // Asumsi format dateStr adalah dd/MM/yyyy (dari DatePicker)
        val inputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val targetDate = try {
            inputFormat.parse(dateStr)
        } catch (e: Exception) {
            return groups
        }

        // Format tanggal target menjadi format yang sama seperti header grup
        val groupDateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val targetGroupHeader = groupDateFormat.format(targetDate)

        // Hanya tampilkan grup yang sesuai
        return groups.filter { it.date == targetGroupHeader }
    }

    private fun filterByType(type: String, groups: List<TransactionGroup>): List<TransactionGroup> {
        return groups.mapNotNull { group ->
            val filteredTransactions = group.transactions.filter { it.type == type }

            if (filteredTransactions.isNotEmpty()) TransactionGroup(group.date, filteredTransactions)
            else null
        }
    }

    // ==================== SETUP RECYCLERVIEW ====================
    private fun setupRecyclerView() {
        // Awalnya kosong
        adapter = RiwayatAdapter(emptyList())
        binding.recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiwayat.adapter = adapter
    }

    // ==================== LOAD DATA (REAL) ====================
    private fun loadTransactionData() {
        // 1. Ambil data transaksi dari Manager
        allTransactionGroups = TransactionManager.getGroupedTransactions()

        // 2. Tampilkan semua data yang belum difilter
        adapter = RiwayatAdapter(allTransactionGroups)
        binding.recyclerViewRiwayat.adapter = adapter

        // 3. Tampilkan pesan kosong jika tidak ada data (jika Anda memiliki TextView kosong di layout)
        if (allTransactionGroups.isEmpty()) {
            // Asumsi Anda punya TextView/layout untuk empty state,
            // contoh: binding.emptyStateText.visibility = View.VISIBLE
        }
    }

    // ==================== CLEANUP ====================
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
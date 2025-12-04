package com.rahmanda.moneyflow.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.databinding.FragmentRiwayatBinding
import com.rahmanda.moneyflow.RiwayatAdapter
import com.rahmanda.moneyflow.Transaction
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
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    // Data asli
    private val allTransactionGroups = mutableListOf<TransactionGroup>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFilters()
        setupRecyclerView()
        loadTransactionData()
    }

    // ==================== SETUP FILTERS ====================
    private fun setupFilters() {
        setupSpinnerTanggalAwal()
        setupSpinnerJenis()
        setupTanggalPicker()
    }

    // 1. SPINNER TANGGAL - SIMPLE
    private fun setupSpinnerTanggalAwal() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_white, // LAYOUT DENGAN TEKS PUTIH
            listOf("Pilih tanggal")
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTanggal.adapter = adapter

        // Set icon warna putih
        binding.ivIconTanggal.setColorFilter(Color.WHITE)
    }

    // 2. SPINNER JENIS - SIMPLE
    private fun setupSpinnerJenis() {
        val jenisList = listOf("Semua", "Pemasukan", "Pengeluaran")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_white, // LAYOUT DENGAN TEKS PUTIH
            jenisList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJenis.adapter = adapter

        // Set selection default
        binding.spinnerJenis.setSelection(0)

        // Set icon awal
        updateJenisIcon(0)

        // Set listener
        binding.spinnerJenis.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}

            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedType = parent?.getItemAtPosition(position).toString()
                updateJenisIcon(position)
                applyFilters()
            }
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

    // 3. DATE PICKER - SIMPLE
    private fun setupTanggalPicker() {
        binding.spinnerTanggal.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                openDatePicker()
            }
            true
        }
    }

    private fun openDatePicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Format tanggal
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

                // Format untuk display
                val displayDate = displayFormat.format(
                    Calendar.getInstance().apply {
                        set(year, month, day)
                    }.time
                )

                // Update spinner
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item_white,
                    listOf(displayDate)
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTanggal.adapter = adapter

                // Apply filter
                applyFilters()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setTitle("Pilih Tanggal")
        // HAPUS BATASAN TANGGAL: dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    // ==================== APPLY FILTERS ====================
    private fun applyFilters() {
        // Filter by date
        var filteredByDate = if (selectedDate.isNotEmpty()) {
            filterByDate(selectedDate)
        } else {
            allTransactionGroups
        }

        // Filter by type
        val filteredByType = if (selectedType != "Semua") {
            filterByType(selectedType, filteredByDate)
        } else {
            filteredByDate
        }

        // Update adapter
        adapter = RiwayatAdapter(filteredByType)
        binding.recyclerViewRiwayat.adapter = adapter
    }

    private fun filterByDate(dateStr: String): List<TransactionGroup> {
        val dateParts = dateStr.split("/")
        if (dateParts.size != 3) return allTransactionGroups

        val selectedDay = dateParts[0] // Tanggal (dd)

        return allTransactionGroups.mapNotNull { group ->
            val filteredTransactions = group.transactions.filter { transaction ->
                transaction.date.split(" ").getOrNull(0) == selectedDay
            }

            if (filteredTransactions.isNotEmpty()) TransactionGroup(group.date, filteredTransactions)
            else null
        }
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
        adapter = RiwayatAdapter(emptyList())
        binding.recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiwayat.adapter = adapter
    }

    // ==================== LOAD DATA ====================
    private fun loadTransactionData() {
        allTransactionGroups.clear()
        allTransactionGroups.addAll(getSampleData())

        adapter = RiwayatAdapter(allTransactionGroups)
        binding.recyclerViewRiwayat.adapter = adapter
    }

    private fun getSampleData(): List<TransactionGroup> {
        return listOf(
            TransactionGroup(
                date = "13 November 2025",
                transactions = listOf(
                    Transaction(
                        id = 1,
                        date = "13 November 2025 12.30",
                        type = "Pengeluaran",
                        category = "Beli Nasi Padang",
                        description = "RPA v 1.4",
                        amount = 20000.0
                    ),
                    Transaction(
                        id = 2,
                        date = "13 November 2025",
                        type = "Pengeluaran",
                        category = "Bayar Gojek",
                        description = "",
                        amount = 15000.0
                    ),
                    Transaction(
                        id = 3,
                        date = "13 November 2025",
                        type = "Pengeluaran",
                        category = "Beli Pulsa",
                        description = "RPPC v 6.0",
                        amount = 20000.0
                    ),
                    Transaction(
                        id = 4,
                        date = "13 November 2025",
                        type = "Pemasukan",
                        category = "Tabungan",
                        description = "MEPC v 7.0",
                        amount = 100000.0
                    )
                )
            ),
            TransactionGroup(
                date = "12 November 2025",
                transactions = listOf(
                    Transaction(
                        id = 5,
                        date = "12 November 2025",
                        type = "Pengeluaran",
                        category = "RPA v 21.14",
                        description = "DTAK v 8.0",
                        amount = 50000.0
                    ),
                    Transaction(
                        id = 6,
                        date = "12 November 2025",
                        type = "Pemasukan",
                        category = "Terjorya",
                        description = "DTAK v 6.0",
                        amount = 50000.0
                    )
                )
            )
        )
    }

    // ==================== CLEANUP ====================
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
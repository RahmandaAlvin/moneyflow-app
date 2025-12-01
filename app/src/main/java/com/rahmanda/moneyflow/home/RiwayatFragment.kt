package com.rahmanda.moneyflow.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.RiwayatAdapter
import com.rahmanda.moneyflow.Transaction
import com.rahmanda.moneyflow.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class RiwayatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatAdapter
    private lateinit var spinnerJenis: Spinner
    private lateinit var editTextTanggal: TextInputEditText
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi views
        recyclerView = view.findViewById(R.id.recyclerViewRiwayat)
        spinnerJenis = view.findViewById(R.id.spinnerJenis)
        editTextTanggal = view.findViewById(R.id.editTextTanggal)

        // Setup spinner jenis transaksi
        setupJenisSpinner()

        // Setup date picker
        setupDatePicker()

        // Setup RecyclerView
        setupRecyclerView()

        // Load data contoh
        loadSampleData()
    }

    private fun setupJenisSpinner() {
        val jenisTransaksi = arrayOf(
            "Semua Jenis",
            "Pemasukan",
            "Pengeluaran"
        )

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            jenisTransaksi
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = adapterSpinner
    }

    private fun setupDatePicker() {
        editTextTanggal.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format tanggal yang dipilih
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                editTextTanggal.setText(dateFormat.format(selectedDate.time))

                // Filter data berdasarkan tanggal yang dipilih
                filterTransactions()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun setupRecyclerView() {
        adapter = RiwayatAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadSampleData() {
        // Data contoh seperti pada gambar
        val sampleData = listOf(
            createTransaction(
                1,
                TransactionType.EXPENSE,
                20000.0,
                "Makanan",
                "Beli Nasi Padang",
                "13-11-2025",
                "12:30"
            ),
            createTransaction(
                2,
                TransactionType.EXPENSE,
                25000.0,
                "Transportasi",
                "Bayar Gojek",
                "13-11-2025",
                "10:30"
            ),
            createTransaction(
                3,
                TransactionType.EXPENSE,
                20000.0,
                "Komunikasi",
                "Beli Pulsa",
                "13-11-2025",
                "08:00"
            ),
            createTransaction(
                4,
                TransactionType.INCOME,
                100000.0,
                "Tabungan",
                "Tabungan",
                "13-11-2025",
                "07:00"
            ),
            createTransaction(
                5,
                TransactionType.EXPENSE,
                50000.0,
                "Utilities",
                "Bayar Listrik",
                "12-11-2025",
                "18:15"
            ),
            createTransaction(
                6,
                TransactionType.INCOME,
                50000.0,
                "Tabungan",
                "Tabungan",
                "12-11-2025",
                "18:00"
            )
        )

        adapter.updateData(sampleData)
    }

    private fun filterTransactions() {
        // Implementasi filter berdasarkan tanggal dan jenis transaksi
        val selectedJenis = spinnerJenis.selectedItemPosition
        val selectedDate = editTextTanggal.text.toString()

        // Filter logic bisa ditambahkan di sini
        // Untuk sekarang, tampilkan semua data
        loadSampleData()
    }

    private fun createTransaction(
        id: Int,
        type: TransactionType,
        amount: Double,
        category: String,
        description: String,
        dateStr: String,
        time: String
    ): Transaction {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateStr) ?: Date()

        return Transaction(id, type, amount, description, category, date, time)
    }
}
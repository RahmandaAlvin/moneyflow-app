package com.rahmanda.moneyflow

import com.rahmanda.moneyflow.transaction.TransactionAdapter
import com.rahmanda.moneyflow.transaction.Transaction
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
class RiwayatActivity : AppCompatActivity()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var spinnerJenis: Spinner
    private lateinit var editTextTanggal: TextInputEditText

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        setupDatePicker()
        setupSpinnerJenis()
        setupRecyclerView()
        loadTransactionData()
    }

    private fun setupDatePicker() {
        editTextTanggal = findViewById(R.id.editTextTanggal)

        // Set default text
        editTextTanggal.setText("Semua Bulan")

        // Click listener untuk membuka date picker
        editTextTanggal.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                val selectedDate = dateFormat.format(calendar.time)
                editTextTanggal.setText(selectedDate)

                // Filter berdasarkan tanggal yang dipilih
                filterByDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Tambahkan button "Semua Bulan
        datePicker.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Semua Bulan") { _, _ ->
            editTextTanggal.setText("Semua Bulan")
            loadAllData()
        }

        datePicker.show()
    }

    private fun setupSpinnerJenis() {
        spinnerJenis = findViewById(R.id.spinnerJenis)

        // Setup Spinner Jenis Transaksi (Kanan) - DENGAN PANAH
        val jenisOptions = arrayOf("Semua Jenis", "Pemasukan", "Pengeluaran")
        val jenisAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisOptions)
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = jenisAdapter

        // Listener untuk Spinner Jenis
        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> loadAllData() // Semua Jenis
                    1 -> filterPemasukan() // Pemasukan saja
                    2 -> filterPengeluaran() // Pengeluaran saja
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadAllData() {
        adapter.submitList(getSampleData())
    }

    private fun filterByDate(date: String) {
        // Filter data berdasarkan tanggal
        // Untuk demo, kita load semua data
        loadAllData()
    }

    private fun filterPemasukan() {
        val filteredData = getSampleData().filter { it.type == "PEMASUKAN" }
        adapter.submitList(filteredData)
    }

    private fun filterPengeluaran() {
        val filteredData = getSampleData().filter { it.type == "PENGELUARAN" }
        adapter.submitList(filteredData)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewRiwayat)
        adapter = TransactionAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadTransactionData() {
        adapter.submitList(getSampleData())
    }

    private fun getSampleData(): List<Transaction> {
        return listOf(
            Transaction(
                type = "PENGELUARAN",
                amount = 20000,
                description = "Beli Nasi Padang",
                date = "13 NOV 12:30"
            ),
            Transaction(
                type = "PENGELUARAN",
                amount = 25000,
                description = "Bayar Gojek",
                date = "13 NOV 10:30"
            ),
            Transaction(
                type = "PEMASUKAN",
                amount = 100000,
                description = "Tabungan",
                date = "13 NOV 07:00"
            )
        )
    }
}

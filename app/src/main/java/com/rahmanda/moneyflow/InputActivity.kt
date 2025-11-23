package com.rahmanda.moneyflow

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.R
import java.text.NumberFormat
import java.util.*

class InputActivity : AppCompatActivity() {

    private lateinit var amount: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var spinnerTanggal: Spinner
    private lateinit var editDeskripsi: EditText
    private lateinit var buttonTambahLagi: Button
    private lateinit var buttonSelesai: Button

    private var selectedDate: String = "Pilih tanggal"
    private var selectedType: String = "Pemasukan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        initViews()
        setupSpinnerKategori()
        setupSpinnerTanggalAwal()
        setupTanggalPicker()
        setupAmountFormatter()
        setupButtons()
    }

    private fun initViews() {
        amount = findViewById(R.id.textAmount)
        spinnerKategori = findViewById(R.id.spinnerKategori)
        spinnerTanggal = findViewById(R.id.spinnerTanggal)
        editDeskripsi = findViewById(R.id.editDeskripsi)
        buttonTambahLagi = findViewById(R.id.buttonTambahLagi)
        buttonSelesai = findViewById(R.id.buttonSelesai)
    }

    // Spinner kategori sekarang pakai layout custom (putih)
    private fun setupSpinnerKategori() {
        val kategoriAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_white,
            listOf("Pemasukan", "Pengeluaran")
        )
        kategoriAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white)

        spinnerKategori.adapter = kategoriAdapter

        spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?,
                position: Int, id: Long
            ) {
                selectedType = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    // Spinner tanggal default (tulisan putih)
    private fun setupSpinnerTanggalAwal() {
        val tanggalAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_white,
            listOf("Pilih tanggal")
        )
        tanggalAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white)

        spinnerTanggal.adapter = tanggalAdapter
    }

    private fun setupTanggalPicker() {
        spinnerTanggal.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) openDatePicker()
            true
        }
    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"

                val adapter = ArrayAdapter(
                    this,
                    R.layout.spinner_item_white,
                    listOf(selectedDate)
                )
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_white)

                spinnerTanggal.adapter = adapter
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun setupAmountFormatter() {
        amount.addTextChangedListener(object : TextWatcher {

            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val clean = s.toString().replace("[^\\d]".toRegex(), "")

                if (clean.isNotEmpty()) {
                    val number = clean.toLong()
                    val formatted = "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID"))
                        .format(number)

                    amount.setText(formatted)
                    amount.setSelection(formatted.length)
                } else {
                    amount.setText("")
                }

                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupButtons() {

        buttonTambahLagi.setOnClickListener {

            val nilaiInput = amount.text.toString().trim()

            if (nilaiInput.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Transaksi sebesar $nilaiInput tersimpan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Tidak ada nominal yang diinput", Toast.LENGTH_SHORT).show()
            }

            // Reset input
            amount.setText("")
            amount.hint = "Rp 0"
            editDeskripsi.setText("")

            setupSpinnerTanggalAwal() // reset tanggal putih kembali
        }

        buttonSelesai.setOnClickListener {
            Toast.makeText(this, "Selesai", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

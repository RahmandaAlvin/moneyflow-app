package com.rahmanda.moneyflow

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.data.SharedPrefManager
import com.rahmanda.moneyflow.data.TransactionManager
import com.rahmanda.moneyflow.home.HomeActivity
import java.text.NumberFormat
import java.util.*

class InputActivity : AppCompatActivity() {

    private lateinit var btnClose: ImageView
    private lateinit var amount: EditText

    // TextView sebagai pengganti Spinner
    private lateinit var textViewKategori: TextView
    private lateinit var textViewTanggal: TextView

    // Ikon Kategori
    private lateinit var ivIconKategori: ImageView

    private lateinit var editDeskripsi: EditText
    private lateinit var buttonTambahLagi: Button
    private lateinit var buttonSelesai: Button

    private lateinit var sharedPrefManager: SharedPrefManager // Untuk ambil username

    private var selectedDateDisplay: String = "Pilih tanggal"
    private var selectedDateObject: Date = Date() // Objek Date untuk penyimpanan
    private var selectedType: String = "Pemasukan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        sharedPrefManager = SharedPrefManager(this)

        initViews()
        setupCloseButton()
        setupKategoriPicker()
        setupTanggalPicker()
        setupAmountFormatter()
        setupButtons()

        // Atur tampilan awal
        textViewKategori.text = selectedType
        textViewTanggal.text = selectedDateDisplay
        updateKategoriIcon(selectedType)
    }

    // ... (initViews, setupCloseButton, setupKategoriPicker, updateKategoriIcon, setupTanggalPicker, setupAmountFormatter)

    /* ======================================================
        INISIALISASI VIEW
    ====================================================== */
    private fun initViews() {
        btnClose = findViewById(R.id.btnClose)
        amount = findViewById(R.id.textAmount)

        // Inisialisasi TextView
        textViewKategori = findViewById(R.id.textViewKategori)
        textViewTanggal = findViewById(R.id.textViewTanggal)

        // Inisialisasi Ikon
        ivIconKategori = findViewById(R.id.ivIconKategori)

        editDeskripsi = findViewById(R.id.editDeskripsi)
        buttonTambahLagi = findViewById(R.id.buttonTambahLagi)
        buttonSelesai = findViewById(R.id.buttonSelesai)
    }

    /* ======================================================
        1. TOMBOL CLOSE
    ====================================================== */
    private fun setupCloseButton() {
        btnClose.setOnClickListener {
            finish()
        }
    }

    /* ======================================================
        2. KATEGORI PICKER (UPDATE ICON)
    ====================================================== */
    private fun setupKategoriPicker() {
        textViewKategori.setOnClickListener {
            val popup = PopupMenu(this, textViewKategori)
            val kategoriList = listOf("Pemasukan", "Pengeluaran")

            kategoriList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            popup.setOnMenuItemClickListener { item ->
                selectedType = item.title.toString()
                textViewKategori.text = selectedType

                updateKategoriIcon(selectedType)

                true
            }
            popup.show()
        }
    }

    // Fungsi Bantu untuk Update Ikon Kategori
    private fun updateKategoriIcon(type: String) {
        when (type) {
            "Pemasukan" -> ivIconKategori.setImageResource(R.drawable.increase)
            "Pengeluaran" -> ivIconKategori.setImageResource(R.drawable.decrease)
            else -> ivIconKategori.setImageResource(R.drawable.increase)
        }
    }

    /* ======================================================
        3. TANGGAL PICKER
    ====================================================== */
    private fun setupTanggalPicker() {
        textViewTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }

                // Simpan objek Date untuk Repository
                selectedDateObject = selectedCalendar.time

                // Format tampilan
                selectedDateDisplay = "$day/${month + 1}/$year"

                textViewTanggal.text = selectedDateDisplay
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    /* ======================================================
        4. FORMAT NOMINAL Rp
    ====================================================== */
    private fun setupAmountFormatter() {
        amount.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val clean = s.toString().replace("[^\\d]".toRegex(), "")

                if (clean.isNotEmpty()) {
                    val number = clean.toLong()
                    val formatted = "Rp " +
                            NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)

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

    /* ======================================================
        FUNGSI UTAMA UNTUK MENYIMPAN TRANSAKSI
    ====================================================== */
    private fun saveTransaction(): Boolean {
        val nominalText = amount.text.toString().trim()
        val description = editDeskripsi.text.toString().trim()
        val username = sharedPrefManager.getUsername() ?: "unknown"

        if (nominalText.isEmpty() || selectedDateDisplay == "Pilih tanggal") {
            Toast.makeText(this, "Nominal dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Hapus format Rupiah dan titik ribuan untuk diubah ke Double
        val cleanAmount = nominalText.replace("[^\\d]".toRegex(), "").toDoubleOrNull()

        if (cleanAmount == null || cleanAmount <= 0) {
            Toast.makeText(this, "Nominal tidak valid.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Buat objek Transaksi
        val newTransaction = Transaction(
            id = System.currentTimeMillis().toInt(),
            amount = cleanAmount,
            type = selectedType,
            category = description, // Menggunakan deskripsi sebagai kategori
            date = selectedDateObject // Menggunakan objek Date yang sudah disimpan
        )

        // Simpan ke Repository
        TransactionManager.addTransaction(this, newTransaction)
        Toast.makeText(this, "Transaksi sebesar Rp ${NumberFormat.getNumberInstance().format(cleanAmount.toInt())} tersimpan.", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun resetInput() {
        amount.setText("")
        amount.hint = "Rp 0"
        editDeskripsi.setText("")

        // Reset Tanggal ke hari ini
        selectedDateDisplay = "Pilih tanggal"
        selectedDateObject = Date()
        textViewTanggal.text = selectedDateDisplay

        // Reset Kategori ke Pemasukan
        selectedType = "Pemasukan"
        textViewKategori.text = selectedType
        updateKategoriIcon(selectedType)
    }

    /* ======================================================
        6. TOMBOL: TAMBAH LAGI & SELESAI
    ====================================================== */
    private fun setupButtons() {

        /* Tombol TAMBAH LAGI (Simpan data, reset input, tetap di halaman) */
        buttonTambahLagi.setOnClickListener {
            if (saveTransaction()) {
                resetInput()
            }
        }

        /* ============================
            TOMBOL SELESAI (Simpan data, navigasi ke Riwayat)
        ============================ */
        buttonSelesai.setOnClickListener {
            if (saveTransaction()) {
                // Setelah data tersimpan → arahkan ke Riwayat
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("open_riwayat", true)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }
        }
    }
}
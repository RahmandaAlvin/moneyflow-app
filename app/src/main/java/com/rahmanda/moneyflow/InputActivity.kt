package com.rahmanda.moneyflow

// Import semua komponen yang diperlukan
import android.app.DatePickerDialog // Untuk memilih tanggal
import android.content.Intent // Untuk pindah halaman (Activity)
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher // Untuk memantau perubahan text jumlah uang
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.data.TransactionManager // Tempat penyimpanan data transaksi (database lokal)
import com.rahmanda.moneyflow.home.HomeActivity // Activity tujuan setelah selesai input
import java.text.NumberFormat // Untuk format angka jadi Rp
import java.util.* // Untuk Date dan Calendar

class InputActivity : AppCompatActivity() {

    // Deklarasi View yang nanti dihubungkan ke XML belum diberi nilai, (harus var, non nullable)
    private lateinit var btnClose: ImageView
    private lateinit var amount: EditText
    private lateinit var textViewKategori: TextView
    private lateinit var textViewTanggal: TextView
    private lateinit var ivIconKategori: ImageView
    private lateinit var editDeskripsi: EditText
    private lateinit var buttonTambahLagi: Button
    private lateinit var buttonSelesai: Button

    // Variabel untuk menyimpan data input (langsung diberi nilai)

    private var selectedDateDisplay: String = "Pilih tanggal"
    private var selectedDateObject: Date = Date()
    private var selectedType: String = "Pemasukan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // menjalankan semua fungsi setup agar tampilan siap digunakan
        initViews()
        setupCloseButton()
        setupKategoriPicker()
        setupTanggalPicker()
        setupAmountFormatter()
        setupButtons()

        // menampilkan nilai default saat activity pertama dibuka
        textViewKategori.text = selectedType
        textViewTanggal.text = selectedDateDisplay
        updateKategoriIcon(selectedType)
    }

    // INISIALISASI VIEW yaitu menghubungkan kode dgn XML //
    private fun initViews() {
        btnClose = findViewById(R.id.btnClose)
        amount = findViewById(R.id.textAmount)
        textViewKategori = findViewById(R.id.textViewKategori)
        textViewTanggal = findViewById(R.id.textViewTanggal)
        ivIconKategori = findViewById(R.id.ivIconKategori)
        editDeskripsi = findViewById(R.id.editDeskripsi)
        buttonTambahLagi = findViewById(R.id.buttonTambahLagi)
        buttonSelesai = findViewById(R.id.buttonSelesai)
    }

    // TOMBOL CLOSE untuk Menutup Activity //
    private fun setupCloseButton() {
        btnClose.setOnClickListener {
            finish()
        }
    }

    // KATEGORI PICKER digunakan untuk Popup Menu memilih pemasukan/pengeluaran //
    private fun setupKategoriPicker() {
        textViewKategori.setOnClickListener {
            val popup = PopupMenu(this, textViewKategori)
            val kategoriList = listOf("Pemasukan", "Pengeluaran")

            // Isi menu popup
            kategoriList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            // Aksi ketika salah satu kategori dipilih
            popup.setOnMenuItemClickListener { item ->
                selectedType = item.title.toString()
                textViewKategori.text = selectedType
                updateKategoriIcon(selectedType)
                true
            }

            popup.show()
        }
    }

    // Ubah ikon sesuai kategori
    private fun updateKategoriIcon(type: String) {
        when (type) {
            "Pemasukan" -> ivIconKategori.setImageResource(R.drawable.increase)
            "Pengeluaran" -> ivIconKategori.setImageResource(R.drawable.decrease)
        }
    }

    /*  PICKER untuk Menampilkan date picker dialog, Menyimpan tanggal untuk ditampilkan & disimpan ke DB */
    private fun setupTanggalPicker() {
        textViewTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()

        // tampilan muncul kalender
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                // Simpan ke Calendar
                calendar.set(year, month, dayOfMonth)

                // Simpan tanggal aslinya sebagai Date DIKIRIM KE DATABASE
                selectedDateObject = calendar.time

                // Format tampilan di layar
                selectedDateDisplay = "$dayOfMonth/${month + 1}/$year"
                textViewTanggal.text = selectedDateDisplay
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    // FORMAT NOMINAL Otomatis jadi format "Rp" //
    private fun setupAmountFormatter() {
        amount.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                // membuang simbol dan pemisah
                val clean = s.toString().replace("[^\\d]".toRegex(), "")

                // untuk memberi batas angka
                if (clean.isNotEmpty()) {
                    val number = clean.toLong()
                    val formatted =
                        "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)

                    amount.setText(formatted)
                    amount.setSelection(formatted.length) // Letak kursor di akhir
                } else {
                    amount.setText("")
                }

                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // SIMPAN TRANSAKSI dengan Ambil semua input user, Validasi, Simpan ke TransactionManager (DB Lokal) //

    //mengambil data dari ui
    private fun saveTransaction(): Boolean {
        val nominalText = amount.text.toString().trim() // edit teks, konversi dengan string
        val description = editDeskripsi.text.toString().trim()

        // Validasi wajib isi
        if (nominalText.isEmpty() || selectedDateDisplay == "Pilih tanggal") { //cek inputan, tanggal kosong
            Toast.makeText(this, "Nominal dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Buang format Rupiah agar bisa disimpan sebagai angka
        val cleanAmount = nominalText.replace("[^\\d]".toRegex(), "").toDoubleOrNull()
        if (cleanAmount == null || cleanAmount <= 0) {
            Toast.makeText(this, "Nominal tidak valid.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Buat data transaksi untuk dikirim ke repository
        val newTransaction = Transaction(
            id = System.currentTimeMillis().toInt(),
            amount = cleanAmount,
            type = selectedType,
            category = description,
            date = selectedDateObject
        )

        // Kirim data ke Repository agar disimpan
        TransactionManager.addTransaction(this, newTransaction)

        Toast.makeText(this, "Transaksi tersimpan!", Toast.LENGTH_SHORT).show()
        return true
    }

    // RESET INPUT untuk tombol TAMBAH LAGI //
    private fun resetInput() {
        amount.setText("")
        amount.hint = "Rp 0"
        editDeskripsi.setText("")
        selectedDateDisplay = "Pilih tanggal"
        selectedDateObject = Date()
        textViewTanggal.text = selectedDateDisplay
        selectedType = "Pemasukan"
        textViewKategori.text = selectedType
        updateKategoriIcon(selectedType)
    }

    // SETUP BUTTONS dengan Tambah Lagi → simpan + reset form, Selesai → simpan + kembali ke Home (langsung buka Riwayat) //
    private fun setupButtons() {

        buttonTambahLagi.setOnClickListener {
            if (saveTransaction()) resetInput()
        }

        buttonSelesai.setOnClickListener {
            if (saveTransaction()) {

                // Navigasi ke Home dan buka tab Riwayat
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("open_riwayat", true)

                // Clear history agar tidak bisa kembali ke Input
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }
        }
    }
}

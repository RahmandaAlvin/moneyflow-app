package com.rahmanda.moneyflow.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.Transaction
import com.rahmanda.moneyflow.data.TransactionManager
import java.text.NumberFormat
import java.util.*

class InputFragment : Fragment() {

    private lateinit var amount: EditText
    private lateinit var textViewKategori: TextView
    private lateinit var textViewTanggal: TextView
    private lateinit var ivIconKategori: ImageView
    private lateinit var editDeskripsi: EditText
    private lateinit var buttonTambahLagi: Button
    private lateinit var buttonSelesai: Button

    private var selectedType = "Pemasukan"
    private var selectedDateDisplay = "Pilih tanggal"
    private var selectedDateObject: Date = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amount = view.findViewById(R.id.textAmount)
        textViewKategori = view.findViewById(R.id.textViewKategori)
        textViewTanggal = view.findViewById(R.id.textViewTanggal)
        ivIconKategori = view.findViewById(R.id.iviconKategori)
        editDeskripsi = view.findViewById(R.id.editDeskripsi)
        buttonTambahLagi = view.findViewById(R.id.buttonTambahLagi)
        buttonSelesai = view.findViewById(R.id.buttonSelesai)

        textViewKategori.text = selectedType
        textViewTanggal.text = selectedDateDisplay

        setupKategoriPicker()
        setupTanggalPicker()
        setupAmountFormatter()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        resetInput()
        amount.requestFocus()
    }

    private fun setupKategoriPicker() {
        textViewKategori.setOnClickListener {
            val popup = PopupMenu(requireContext(), textViewKategori)
            val kategoriList = listOf("Pemasukan", "Pengeluaran")

            kategoriList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            popup.setOnMenuItemClickListener {
                selectedType = it.title.toString()
                textViewKategori.text = selectedType
                updateKategoriIcon(selectedType)
                true
            }
            popup.show()
        }
    }

    private fun updateKategoriIcon(type: String) {
        when (type) {
            "Pemasukan" -> ivIconKategori.setImageResource(R.drawable.increase)
            "Pengeluaran" -> ivIconKategori.setImageResource(R.drawable.decrease)
        }
    }

    private fun setupTanggalPicker() {
        textViewTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDateObject = calendar.time
                    selectedDateDisplay = "$dayOfMonth/${month + 1}/$year"
                    textViewTanggal.text = selectedDateDisplay
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
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
                    val formatted =
                        "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
                    amount.setText(formatted)
                    amount.setSelection(formatted.length)
                }
                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun saveTransaction(): Boolean {
        val nominalText = amount.text.toString().trim()
        val description = editDeskripsi.text.toString().trim()

        if (nominalText.isEmpty() || selectedDateDisplay == "Pilih tanggal") {
            Toast.makeText(requireContext(), "Nominal dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show()
            return false
        }

        val cleanAmount = nominalText.replace("[^\\d]".toRegex(), "").toDoubleOrNull() ?: return false

        val newTransaction = Transaction(
            id = System.currentTimeMillis().toInt(),
            amount = cleanAmount,
            type = selectedType,
            category = description,
            date = selectedDateObject
        )

        TransactionManager.addTransaction(requireContext(), newTransaction)
        return true
    }

    private fun resetInput() {
        amount.setText("")
        amount.hint = "Rp 0"
        editDeskripsi.setText("")
        selectedType = "Pemasukan"
        textViewKategori.text = selectedType
        updateKategoriIcon(selectedType)
        selectedDateDisplay = "Pilih tanggal"
        textViewTanggal.text = selectedDateDisplay
    }

    private fun setupButtons() {
        buttonTambahLagi.setOnClickListener {
            if (saveTransaction()) {
                val nominalText = amount.text.toString().trim()
                Toast.makeText(
                    requireContext(),
                    "Transaksi sebesar $nominalText tersimpan!",
                    Toast.LENGTH_SHORT
                ).show()
                resetInput()
            }
        }

        buttonSelesai.setOnClickListener {
            if (saveTransaction()) {
                (activity as HomeActivity).goToRiwayat()
            }
        }
    }
}

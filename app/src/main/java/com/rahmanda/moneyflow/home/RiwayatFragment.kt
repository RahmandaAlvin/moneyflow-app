package com.rahmanda.moneyflow.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.data.TransactionManager
import com.rahmanda.moneyflow.RiwayatAdapter
import com.rahmanda.moneyflow.TransactionGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RiwayatFragment : Fragment() {

    // 1. Deklarasi View dengan findViewById()
    private lateinit var adapter: RiwayatAdapter
    private lateinit var textViewTanggal: TextView
    private lateinit var textViewJenis: TextView
    private lateinit var ivIconTanggal: ImageView
    private lateinit var ivIconJenis: ImageView
    private lateinit var recyclerViewRiwayat: RecyclerView

    // selectedDate akan menyimpan format dd/MM/yyyy untuk filtering
    private var selectedDate: String = ""
    private var selectedType: String = "Semua"

    private val calendar = Calendar.getInstance()
    // Format Display sesuai permintaan: dd/MM/yyyy (Contoh: 07/12/2025)
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    // Format untuk konversi internal: dd/MM/yyyy (Tetap sama)
    private val filterFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var allTransactionGroups = listOf<TransactionGroup>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 2. Inflate Layout secara Manual
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)

        TransactionManager.initialize(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Inisialisasi View dengan findViewById()
        textViewTanggal = view.findViewById(R.id.textViewTanggal)
        textViewJenis = view.findViewById(R.id.textViewJenis)
        ivIconTanggal = view.findViewById(R.id.ivIconTanggal)
        ivIconJenis = view.findViewById(R.id.ivIconJenis)
        recyclerViewRiwayat = view.findViewById(R.id.recyclerViewRiwayat)

        textViewTanggal.text = "Pilih tanggal"
        textViewJenis.text = "Semua"

        setupFilters()
        setupRecyclerView()
        loadTransactionData()
    }

    override fun onResume() {
        super.onResume()
        loadTransactionData()
    }

    private fun setupFilters() {
        setupJenisPicker()
        setupTanggalPicker()
    }

    private fun setupJenisPicker() {
        updateJenisIcon(0)

        // Mengganti binding.textViewJenis dengan textViewJenis
        textViewJenis.setOnClickListener {
            val popup = PopupMenu(requireContext(), textViewJenis)
            val jenisList = listOf("Semua", "Pemasukan", "Pengeluaran")

            jenisList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            popup.setOnMenuItemClickListener { item ->
                selectedType = item.title.toString()
                textViewJenis.text = selectedType
                val position = jenisList.indexOf(selectedType)
                updateJenisIcon(position)
                applyFilters()
                true
            }
            popup.show()
        }
    }

    private fun updateJenisIcon(position: Int) {
        when (position) {
            0 -> {
                ivIconJenis.setImageResource(R.drawable.semua)
                ivIconJenis.setColorFilter(Color.WHITE)
            }
            1 -> {
                ivIconJenis.setImageResource(R.drawable.increase)
                ivIconJenis.setColorFilter(Color.WHITE)
            }
            2 -> {
                ivIconJenis.setImageResource(R.drawable.decrease)
                ivIconJenis.setColorFilter(Color.WHITE)
            }
        }
    }

    private fun setupTanggalPicker() {
        ivIconTanggal.setColorFilter(Color.WHITE)

        textViewTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    // =======================================================
    // KODE DATE PICKER DENGAN LOGIKA onDateSetListener EKSPLISIT
    // =======================================================
    private fun openDatePicker() {
        // Menggunakan sintaks objek untuk onDateSetListener yang eksplisit
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: android.widget.DatePicker?,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {
                // 1. Set Calendar dengan tanggal yang dipilih
                calendar.set(year, month, dayOfMonth)

                // 2. Format untuk Tampilan (Contoh: 07/12/2025)
                val displayDate = displayFormat.format(calendar.time)

                // 3. Format untuk Filter Internal (Sama dengan format display)
                selectedDate = filterFormat.format(calendar.time)

                // 4. Update Teks View
                textViewTanggal.text = displayDate

                // 5. Terapkan filter
                applyFilters()
            }
        }

        val dialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.setTitle("Pilih Tanggal")
        dialog.show()
    }

    private fun applyFilters() {
        var filteredGroups = allTransactionGroups
        if (selectedDate.isNotEmpty() && selectedDate != "Pilih tanggal") {
            filteredGroups = filterByDate(selectedDate, filteredGroups)
        }
        val finalFilteredGroups = if (selectedType != "Semua") {
            filterByType(selectedType, filteredGroups)
        } else {
            filteredGroups
        }
        adapter = RiwayatAdapter(finalFilteredGroups)
        recyclerViewRiwayat.adapter = adapter
    }

    private fun filterByDate(dateStr: String, groups: List<TransactionGroup>): List<TransactionGroup> {
        // Format input (dd/MM/yyyy)
        val inputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val targetDate = try {
            inputFormat.parse(dateStr)
        } catch (e: Exception) {
            return groups
        }

        // Format Header Grup (dd MMMM yyyy)
        val groupDateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val targetGroupHeader = groupDateFormat.format(targetDate)

        return groups.filter { it.date == targetGroupHeader }
    }

    private fun filterByType(type: String, groups: List<TransactionGroup>): List<TransactionGroup> {
        return groups.mapNotNull { group ->
            val filteredTransactions = group.transactions.filter { it.type == type }

            if (filteredTransactions.isNotEmpty()) TransactionGroup(group.date, filteredTransactions)
            else null
        }
    }
    private fun setupRecyclerView() {
        adapter = RiwayatAdapter(emptyList())
        recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRiwayat.adapter = adapter
    }
    private fun loadTransactionData() {
        allTransactionGroups = TransactionManager.getGroupedTransactions()
        adapter = RiwayatAdapter(allTransactionGroups)
        recyclerViewRiwayat.adapter = adapter
        if (allTransactionGroups.isEmpty()) {
        }
    }

    // Fungsi onDestroyView() yang terkait dengan binding sudah dihapus
}
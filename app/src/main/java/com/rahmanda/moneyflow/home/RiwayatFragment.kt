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

    private var selectedDate: String = ""
    private var selectedType: String = "Semua"

    private val calendar = Calendar.getInstance()
    private val displayFormat = SimpleDateFormat("dd MM yyyy", Locale("id", "ID"))

    private var allTransactionGroups = listOf<TransactionGroup>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)

        TransactionManager.initialize(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewTanggal.text = "Pilih tanggal"
        binding.textViewJenis.text = "Semua"

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

        binding.textViewJenis.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.textViewJenis)
            val jenisList = listOf("Semua", "Pemasukan", "Pengeluaran")

            jenisList.forEachIndexed { index, item ->
                popup.menu.add(0, index, index, item)
            }

            popup.setOnMenuItemClickListener { item ->
                selectedType = item.title.toString()
                binding.textViewJenis.text = selectedType
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
                binding.ivIconJenis.setImageResource(R.drawable.semua)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
            1 -> {
                binding.ivIconJenis.setImageResource(R.drawable.increase)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
            2 -> {
                binding.ivIconJenis.setImageResource(R.drawable.decrease)
                binding.ivIconJenis.setColorFilter(Color.WHITE)
            }
        }
    }

    private fun setupTanggalPicker() {
        binding.ivIconTanggal.setColorFilter(Color.WHITE)

        binding.textViewTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                val displayDate = displayFormat.format(
                    Calendar.getInstance().apply {
                        set(year, month, day)
                    }.time
                )
                binding.textViewTanggal.text = displayDate
                applyFilters()
            },
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
        binding.recyclerViewRiwayat.adapter = adapter
    }

    private fun filterByDate(dateStr: String, groups: List<TransactionGroup>): List<TransactionGroup> {
        val inputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val targetDate = try {
            inputFormat.parse(dateStr)
        } catch (e: Exception) {
            return groups
        }
        val groupDateFormat = java.text.SimpleDateFormat("dd MM yyyy", Locale("id", "ID"))
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
        binding.recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiwayat.adapter = adapter
    }
    private fun loadTransactionData() {
        allTransactionGroups = TransactionManager.getGroupedTransactions()
        adapter = RiwayatAdapter(allTransactionGroups)
        binding.recyclerViewRiwayat.adapter = adapter
        if (allTransactionGroups.isEmpty()) {
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
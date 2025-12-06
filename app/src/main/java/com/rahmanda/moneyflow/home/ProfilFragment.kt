package com.rahmanda.moneyflow.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.rahmanda.moneyflow.LoginActivity
import com.rahmanda.moneyflow.R
import com.rahmanda.moneyflow.data.SharedPrefManager

class ProfilFragment : Fragment() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        sharedPrefManager = SharedPrefManager(requireContext())

        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Aksi logout
        btnLogout.setOnClickListener {
            // 1. Bersihkan sesi (Username)
            sharedPrefManager.clearSession()

            // 2. Arahkan ke LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)

            // Agar tidak bisa kembali ke halaman sebelumnya
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }

        return view
    }
}
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

class ProfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        // Ambil button logout
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Aksi logout
        btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)

            // Agar tidak bisa kembali ke halaman sebelumnya
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }

        return view
    }
}

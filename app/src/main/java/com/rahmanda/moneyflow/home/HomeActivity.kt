package com.rahmanda.moneyflow.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rahmanda.moneyflow.InputActivity
import com.rahmanda.moneyflow.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragmentContainerView)

        bottomNavigationView.setupWithNavController(navController)

        val openRiwayat = intent.getBooleanExtra("open_riwayat", false)
        if (openRiwayat) {
            bottomNavigationView.selectedItemId = R.id.riwayatFragment
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.InputActivity -> {
                    val intent = Intent(this, InputActivity::class.java)
                    startActivity(intent)
                    false
                }

                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }
}

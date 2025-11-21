package com.rahmanda.moneyflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.home.HomeActivity

class   LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.editTextUsername)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.butonLogin)

        btnLogin.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            } else if (user == "alvin" && pass == "1234") {
                Toast.makeText(this, "Login berhasil! Selamat datang, $user", Toast.LENGTH_LONG).show()

                // Pindah ke HomeActivity setelah login berhasil
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Tutup LoginActivity agar tidak bisa back

            } else {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
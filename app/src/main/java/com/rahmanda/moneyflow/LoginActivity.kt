package com.rahmanda.moneyflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rahmanda.moneyflow.data.SharedPrefManager
import com.rahmanda.moneyflow.data.TransactionManager
import com.rahmanda.moneyflow.home.HomeActivity

class LoginActivity : AppCompatActivity() { //Layar Utama yang ditampilkan
    private lateinit var sharedPrefManager: SharedPrefManager // di gunakan untuk menyimpan data username ketika login berhasil

    override fun onCreate(savedInstanceState: Bundle?) { // fungsi yang digunakan pertama kali di Login Activity

        super.onCreate(savedInstanceState) //  digunakan untuk memanggil on create yang sudah di buat di atas

        setContentView(R.layout.activity_login) // digunakan untuk menghubungkan activity_login.xml di layout

        sharedPrefManager = SharedPrefManager(this) // digunakan untuk membuat objek sharedprefmanager supaya username terbaca

        TransactionManager.initialize(this) // digunakan untuk memanggil TransactionManager. di dalam Transaction Manager tersebut saldo dan riwayat pertama kali isinya 0.

        val username = findViewById<EditText>(R.id.editTextUsername)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.butonLogin)

        btnLogin.setOnClickListener {
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            } else if (user == "alvin" && pass == "1234") {
                sharedPrefManager.saveUsername(user)
                Toast.makeText(this, "Login berhasil! Selamat datang, $user", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
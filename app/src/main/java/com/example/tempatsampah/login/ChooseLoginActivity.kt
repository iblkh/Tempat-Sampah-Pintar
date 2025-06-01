package com.example.tempatsampah.choose_login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tempatsampah.databinding.ActivityChooseLoginBinding
import com.example.tempatsampah.login.LoginActivity // Import LoginActivity

class ChooseLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set fullscreen mode (optional)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityChooseLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Sembunyikan ActionBar jika ada

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLoginAdmin.setOnClickListener {
            // Arahkan ke LoginActivity untuk Admin
            val intent = Intent(this, LoginActivity::class.java)
            // Opsional: Kirim data tambahan jika Anda ingin membedakan Admin/Mahasiswa di LoginActivity
            intent.putExtra("user_type", "admin")
            startActivity(intent)
            finish() // Selesaikan activity ini agar tidak bisa kembali ke sini dari LoginActivity
        }

        binding.btnLoginMahasiswa.setOnClickListener {
            // Arahkan ke LoginActivity untuk Mahasiswa
            val intent = Intent(this, LoginActivity::class.java)
            // Opsional: Kirim data tambahan jika Anda ingin membedakan Admin/Mahasiswa di LoginActivity
            intent.putExtra("user_type", "mahasiswa")
            startActivity(intent)
            finish() // Selesaikan activity ini
        }
    }
}
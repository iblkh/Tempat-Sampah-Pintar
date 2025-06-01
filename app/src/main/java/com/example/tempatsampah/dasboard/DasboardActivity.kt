package com.example.tempatsampah.dasboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tempatsampah.R
import com.example.tempatsampah.databinding.ActivityDasboardBinding
import com.example.tempatsampah.login.LoginActivity
import android.util.Log // Import ini untuk menggunakan Log.d

class DasboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDasboardBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "DasboardActivity" // Tag untuk logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called: DasboardActivity is starting.") // Log saat onCreate dipanggil

        // Set fullscreen mode (optional)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initializeSharedPreferences()
        displayUserInfo()
        setupClickListeners()
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        Log.d(TAG, "SharedPreferences initialized.") // Log setelah SharedPreferences diinisialisasi
    }

    private fun displayUserInfo() {
        val userName = sharedPreferences.getString("user_name", "User")
        val userEmail = sharedPreferences.getString("user_email", "")

        // Log untuk memverifikasi data yang diambil
        Log.d(TAG, "Retrieved user name from SharedPreferences: $userName")
        Log.d(TAG, "Retrieved user email from SharedPreferences: $userEmail")

        // Display welcome message
        binding.welcomeTextView.text = "Selamat datang, $userName!"
        binding.emailTextView.text = userEmail
    }

    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners.") // Log saat click listener diatur
        // Logout button
        binding.logoutButton.setOnClickListener {
            logout()
        }

        // Feature cards
        binding.feature1Card.setOnClickListener {
            showToast("Fitur Monitoring Sampah")
            Log.d(TAG, "Feature 1 card clicked.")
        }

        binding.feature2Card.setOnClickListener {
            showToast("Fitur Riwayat Aktivitas")
            Log.d(TAG, "Feature 2 card clicked.")
        }

        // Quick action buttons
        binding.refreshButton.setOnClickListener {
            refreshData()
            Log.d(TAG, "Refresh button clicked.")
        }

        binding.settingsButton.setOnClickListener {
            showToast("Pengaturan")
            Log.d(TAG, "Settings button clicked.")
        }
    }

    private fun logout() {
        // Clear login status
        sharedPreferences.edit().apply {
            putBoolean("is_logged_in", false)
            apply()
        }

        showToast("Logout berhasil")
        Log.d(TAG, "Logout successful. Navigating to LoginActivity.")

        // Navigate back to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun refreshData() {
        showToast("Memperbarui data...")
        Log.d(TAG, "Refreshing data initiated.")
        // Add refresh functionality here
        // For example: reload data from server, update UI, etc.
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // Override back button to prevent going back to login
        // Show confirmation dialog if needed
        // Jika Anda ingin menonaktifkan tombol kembali, hapus super.onBackPressed()
        // Namun, biasanya lebih baik menampilkan dialog konfirmasi atau menonaktifkan
        // hanya jika memang dibutuhkan dalam alur aplikasi.
        super.onBackPressed() // Membiarkan tombol kembali berfungsi normal (kembali ke layar sebelumnya)
    }
}
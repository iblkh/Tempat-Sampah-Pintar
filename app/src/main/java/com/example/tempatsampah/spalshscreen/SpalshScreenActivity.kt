package com.example.tempatsampah.spalshscreen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.tempatsampah.R
import com.example.tempatsampah.login.LoginActivity
import com.example.tempatsampah.dasboard.DasboardActivity
import com.example.tempatsampah.choose_login.ChooseLoginActivity // <--- Import ChooseLoginActivity

class SplashScreenActivity : AppCompatActivity() { // Pastikan nama kelas ini konsisten dengan manifest
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set fullscreen mode
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set the content view to your splash screen layout
        setContentView(R.layout.activity_spalsh_screen)

        // Hide the action bar for fullscreen experience
        supportActionBar?.hide()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Initialize views
        val splashImage = findViewById<ImageView>(R.id.iv_logo)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Start animations
        splashImage.startAnimation(fadeIn)

        // Use Handler to delay the transition to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 4000) // 4 seconds delay
    }





    private fun checkUserStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        val intent = if (isLoggedIn) {
            // User sudah login, langsung ke dashboard
            Intent(this, DasboardActivity::class.java)
        } else {
            // User belum login, arahkan ke ChooseLoginActivity
            Intent(this, ChooseLoginActivity::class.java) // <--- PERBAIKAN DI SINI
        }

        startActivity(intent)
        finish() // Finish splash screen agar tidak ada di back stack
    }
}
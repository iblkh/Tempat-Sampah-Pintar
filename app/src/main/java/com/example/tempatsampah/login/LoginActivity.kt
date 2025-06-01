package com.example.tempatsampah.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tempatsampah.R
import com.example.tempatsampah.databinding.ActivityLoginBinding
import com.example.tempatsampah.register.RegisterActivity
import com.example.tempatsampah.dasboard.DasboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var animatorSet: AnimatorSet
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initializeSharedPreferences()
        setupAnimation()
        setupClickListeners()
        setupPasswordToggle()

        // Check for registration data and pre-fill fields
        intent.getStringExtra("registered_email")?.let { email ->
            binding.emailEditText.setText(email)
        }
        intent.getStringExtra("registered_password")?.let { password ->
            binding.passwordEditText.setText(password)
        }
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
    }

    private fun setupAnimation() {
        val imageView = binding.imgIllustration

        val upAnimation = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -30f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
        }

        val rotateAnimation = ObjectAnimator.ofFloat(imageView, "rotation", -3f, 3f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
        }

        animatorSet = AnimatorSet().apply {
            playTogether(upAnimation, rotateAnimation)
            start()
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            handleLogin()
        }

        binding.labelSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupPasswordToggle() {
        binding.passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawable = binding.passwordEditText.compoundDrawables[drawableEnd]
                if (drawable != null && event.rawX >= (binding.passwordEditText.right - drawable.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    // --- PERBAIKAN DI SINI: Ikon untuk visibility off/on sudah dikoreksi ---
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) { // Password akan terlihat
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_lock, // Ikon kunci
                0,
                R.drawable.ic_visibility, // Menggunakan ic_visibility_off saat password terlihat
                0
            )
        } else { // Password akan tersembunyi
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_lock, // Ikon kunci
                0,
                R.drawable.ic_visibility, // Menggunakan ic_visibility saat password tersembunyi
                0
            )
        }
        // Pindahkan kursor ke akhir teks
        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
    }
    // --- AKHIR PERBAIKAN ---

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        when {
            email.isEmpty() -> { showToast(getString(R.string.toast_email_empty)) }
            password.isEmpty() -> { showToast(getString(R.string.toast_password_empty)) }
            !isValidEmail(email) -> { showToast(getString(R.string.toast_email_invalid)) }
            else -> {
                loginUser(email, password)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        setInputsEnabled(false)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.GONE
            setInputsEnabled(true)

            val isRegistered = sharedPreferences.getBoolean("is_registered", false)
            val savedEmail = sharedPreferences.getString("user_email", "")
            val savedPassword = sharedPreferences.getString("user_password", "")

            if (isRegistered && email == savedEmail && password == savedPassword) {
                sharedPreferences.edit().apply {
                    putBoolean("is_logged_in", true)
                    apply()
                }

                showToast(getString(R.string.toast_login_success))
                val intent = Intent(this, DasboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showToast(getString(R.string.toast_login_failed))
            }
        }, 2000)
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.loginButton.isEnabled = enabled
        binding.emailEditText.isEnabled = enabled
        binding.passwordEditText.isEnabled = enabled
        binding.labelSignup.isEnabled = enabled
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::animatorSet.isInitialized) {
            animatorSet.cancel()
        }
    }
}
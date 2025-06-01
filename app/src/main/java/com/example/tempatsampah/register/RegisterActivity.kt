package com.example.tempatsampah.register

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
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tempatsampah.R
import com.example.tempatsampah.login.LoginActivity
// import com.example.tempatsampah.main.MainActivity // No longer needed for direct navigation after register

import com.example.tempatsampah.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var animatorSet: AnimatorSet
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeSharedPreferences()
        setupAnimation()
        setupClickListeners()
        setupPasswordToggles()
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
        binding.registerButton.setOnClickListener {
            handleRegistration()
        }

        binding.loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupPasswordToggles() {
        binding.passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawable = binding.passwordEditText.compoundDrawables[drawableEnd]
                if (drawable != null && event.rawX >= (binding.passwordEditText.right - drawable.bounds.width())) {
                    togglePasswordVisibility(binding.passwordEditText, isPasswordVisible, R.drawable.ic_lock)
                    isPasswordVisible = !isPasswordVisible
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.confirmPasswordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawable = binding.confirmPasswordEditText.compoundDrawables[drawableEnd]
                if (drawable != null && event.rawX >= (binding.confirmPasswordEditText.right - drawable.bounds.width())) {
                    togglePasswordVisibility(binding.confirmPasswordEditText, isConfirmPasswordVisible, R.drawable.ic_lock)
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    // --- PERBAIKAN DI SINI: Ikon untuk visibility off/on ---
    private fun togglePasswordVisibility(editText: EditText, currentVisibilityState: Boolean, drawableStartResId: Int) {
        val newVisibilityState = !currentVisibilityState
        if (newVisibilityState) { // Password akan terlihat
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawableStartResId,
                0,
                R.drawable.ic_visibility, // Gunakan ikon mata tertutup/dicoret
                0
            )
        } else { // Password akan tersembunyi
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawableStartResId,
                0,
                R.drawable.ic_visibility, // Gunakan ikon mata terbuka
                0
            )
        }
        // Pindahkan kursor ke akhir teks
        editText.setSelection(editText.text.length)
    }
    // --- AKHIR PERBAIKAN ---

    private fun handleRegistration() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        when {
            name.isEmpty() -> { showToast(getString(R.string.toast_name_empty)) }
            email.isEmpty() -> { showToast(getString(R.string.toast_email_empty)) }
            password.isEmpty() -> { showToast(getString(R.string.toast_password_empty)) }
            !isValidEmail(email) -> { showToast(getString(R.string.toast_email_invalid)) }
            password.length < 6 -> { showToast(getString(R.string.toast_password_short)) }
            confirmPassword.isEmpty() -> { showToast(getString(R.string.toast_confirm_password_empty)) }
            password != confirmPassword -> { showToast(getString(R.string.toast_password_mismatch)) }
            else -> {
                registerUser(name, email, password)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        setInputsEnabled(false)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.GONE
            setInputsEnabled(true)

            sharedPreferences.edit().apply {
                putString("user_name", name)
                putString("user_email", email)
                putString("user_password", password)
                putBoolean("is_registered", true)
                apply()
            }

            showToast(getString(R.string.toast_registration_success))

            // Ini adalah bagian yang mengarahkan ke LoginActivity setelah registrasi berhasil
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("registered_email", email)
                putExtra("registered_password", password)
            }
            startActivity(intent)
            finish()

        }, 2000) // Simulasi waktu proses registrasi (2 detik)
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.nameEditText.isEnabled = enabled
        binding.emailEditText.isEnabled = enabled
        binding.passwordEditText.isEnabled = enabled
        binding.confirmPasswordEditText.isEnabled = enabled
        binding.registerButton.isEnabled = enabled
        binding.loginTextView.isEnabled = enabled
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
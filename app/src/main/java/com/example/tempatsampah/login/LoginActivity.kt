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
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
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
        setupEditTextPlaceholders()
//        setupAutoNavigation() // Tambah fungsi ini

        // Check for registration data and pre-fill fields
        intent.getStringExtra("registered_email")?.let { email ->
            binding.emailEditText.setText(email)
        }
        intent.getStringExtra("registered_password")?.let { password ->
            binding.passwordEditText.setText(password)
        }
    }

    // ... di luar onCreate(), sebagai private fun
    private fun setupEditTextPlaceholders() {
        // Mendapatkan nilai string dari resources
        val initialEmailText = getString(R.string.tombol_masukan_email) // Atau R.string.initial_email_text jika ada
        val initialPasswordText = getString(R.string.tombol_masukan_password)// Atau ambil dari R.string jika sudah ada

        // Flag untuk melacak apakah teks awal sudah dihapus untuk masing-masing EditText
        var isEmailInitialTextCleared = false
        var isPasswordInitialTextCleared = false

        // Email EditText listeners
        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Saat EditText mendapatkan fokus
                if (!isEmailInitialTextCleared && binding.emailEditText.text.toString() == initialEmailText) {
                    binding.emailEditText.setText("") // Hapus teks awal
                    isEmailInitialTextCleared = true // Set flag bahwa teks sudah dihapus
                }
            } else {
                // Saat EditText kehilangan fokus
                if (binding.emailEditText.text.toString().isEmpty()) {
                    binding.emailEditText.setText(initialEmailText) // Kembalikan teks awal jika kosong
                    isEmailInitialTextCleared = false // Reset flag
                }
            }
        }

        // Tidak perlu setOnClickListener di sini jika OnFocusChangeListener sudah menangani.
        // Cukup biarkan OnFocusChangeListener yang mengontrol behavior.

        // Password EditText listeners
        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Saat EditText mendapatkan fokus
                if (!isPasswordInitialTextCleared && binding.passwordEditText.text.toString() == initialPasswordText) {
                    binding.passwordEditText.setText("") // Hapus teks awal
                    isPasswordInitialTextCleared = true // Set flag
                }
            } else {
                // Saat EditText kehilangan fokus
                if (binding.passwordEditText.text.toString().isEmpty()) {
                    binding.passwordEditText.setText(initialPasswordText) // Kembalikan teks awal jika kosong
                    isPasswordInitialTextCleared = false // Reset flag
                }
            }
        }
    }
// ...

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
                } else {
                    // Jika tidak menyentuh icon, hapus placeholder jika ada
                    if (binding.passwordEditText.text.toString() == "Masukan Password") {
                        binding.passwordEditText.setText("")
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_lock,
                0,
                R.drawable.ic_visibility,
                0
            )
        } else {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_lock,
                0,
                R.drawable.ic_visibility,
                0
            )
        }
        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        when {
            email.isEmpty() || email == "Masukan Email" -> {
                showToast(getString(R.string.toast_email_empty))
                binding.emailEditText.requestFocus()
            }
            password.isEmpty() || password == "Masukan Password" -> {
                showToast(getString(R.string.toast_password_empty))
                binding.passwordEditText.requestFocus()
            }
            !isValidEmail(email) -> {
                showToast(getString(R.string.toast_email_invalid))
                binding.emailEditText.requestFocus()
            }
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
                    putBoolean("is_logged_in", false)
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
package com.sample.hospitaladmin.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sample.common.isMobile
import com.sample.common.isPassword
import com.sample.hospitaladmin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.loginPhoneNumberInput.text.toString().trim()
            val password = binding.loginPasswordInput.text.toString().trim()

            if (!phoneNumber.isMobile() || !password.isPassword()) {
                Snackbar.make(
                    binding.root,
                    "PhoneNumber or Password is not valid", Snackbar.LENGTH_LONG,
                ).apply {
                    setAction("Dismiss") { }
                }.show()
                return@setOnClickListener
            }
            // Check
        }
    }
}
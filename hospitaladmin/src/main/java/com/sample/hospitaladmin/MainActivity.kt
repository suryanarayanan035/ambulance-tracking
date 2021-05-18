package com.sample.hospitaladmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hospitaladmin.auth.AuthActivity
import com.sample.hospitaladmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navigateToAuthBtn.setOnClickListener {
            val authIntent = Intent(this, AuthActivity::class.java)
            startActivity(authIntent)
        }
    }
}
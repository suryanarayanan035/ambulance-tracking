package com.sample.hospitaladmin.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityChooseBinding

class ChooseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.hospitalOption.setOnClickListener {
            val hospitalIntent = Intent(this, AuthActivity::class.java)
            startActivity(hospitalIntent)
        }
        binding.hospitalOption.setOnClickListener {
            val ambulanceIntent = Intent(this, LoginActivity::class.java)
            startActivity(ambulanceIntent)
        }
    }
}
package com.sample.ambulancetracking.tracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.ambulancetracking.databinding.ActivityRequestBinding

class RequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
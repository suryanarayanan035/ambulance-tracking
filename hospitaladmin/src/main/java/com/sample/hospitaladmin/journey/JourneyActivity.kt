package com.sample.hospitaladmin.journey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hospitaladmin.databinding.ActivityJourneyBinding

class JourneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJourneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
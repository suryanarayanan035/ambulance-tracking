package com.sample.ambulancetracking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityMainBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.ambulancetracking.journey.JourneyActivity
import com.sample.ambulancetracking.tracking.UserLocationTracking

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
        binding.navigateToJourneyBtn.setOnClickListener {
            val journeyIntent = Intent(this, JourneyActivity::class.java)
            startActivity(journeyIntent)
        }
        binding.navigateToHomeBtn.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
        }
    }
}
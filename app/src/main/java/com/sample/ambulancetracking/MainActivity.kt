package com.sample.ambulancetracking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityMainBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.ambulancetracking.home.HomeScreen
import com.sample.ambulancetracking.journey.JourneyActivity
import com.sample.ambulancetracking.journey.ListUserRequests
import com.sample.ambulancetracking.journey.RequestBasicDetails
import com.sample.ambulancetracking.journey.RequestLocationDetails
import com.sample.ambulancetracking.search.ListAmbulancesActivitu
import com.sample.ambulancetracking.search.SearchAmbulanceActivity
import com.sample.ambulancetracking.tracking.UserLocationTracking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val homeIntent = Intent(this,HomeScreen::class.java)
        startActivity(homeIntent)
    }


}
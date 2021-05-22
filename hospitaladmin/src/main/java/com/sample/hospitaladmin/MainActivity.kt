package com.sample.hospitaladmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.hospitaladmin.auth.AuthActivity
import com.sample.hospitaladmin.databinding.ActivityMainBinding
import com.sample.hospitaladmin.home.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectionScreenIntent = Intent(this,ListPendingRequestsHospital::class.java)
        startActivity(selectionScreenIntent)
    }
}
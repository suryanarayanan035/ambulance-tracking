package com.sample.hospitaladmin.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.hospitaladmin.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {

    lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {

                } else {
                }
            }
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) -> { }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
            binding.getLocationButton.setOnClickListener{
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {location->
                    binding.latitude.text = location.latitude.toString()
                }
            }
        }
    }



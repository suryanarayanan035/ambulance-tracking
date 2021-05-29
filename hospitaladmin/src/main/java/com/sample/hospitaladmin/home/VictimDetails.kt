package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat.checkSelfPermission
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sample.common.*
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityVictimDetailsBinding
import com.sample.hospitaladmin.home.models.*
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlin.Exception

class VictimDetails : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(
        HospitalService::class.java
    )
    private lateinit var binding: ActivityVictimDetailsBinding
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVictimDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        val ambulanceId = sharedPref.getString(AMBULANCEID_PREFS,"") as String
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                var requestDetailsResponse: GetRequestDetailsByAmbulanceResponse
                withContext(Dispatchers.IO) {
                    requestDetailsResponse = hospitalService.getRequestDetailsByAmbulance(ambulanceId)
                }

                if (requestDetailsResponse.hasError) {
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                } else {
                    binding.root.visibility = View.VISIBLE
                    println(requestDetailsResponse)

                        with(requestDetailsResponse.request)
                        {
                            binding.victimNameValue.text = name
                            binding.victimAgeValue.text = age.toString()
                            binding.victimBloodGroupValue.text = bloodGroup
                            binding.victimMobileValue.text = requestedBy
                            binding.victimAccidentValue.text = if (isAccident) "Yes" else "No"
                            binding.victimGenderValue.text = gender

                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.root.snackbar(_500, DISMISS)
            }
        }

    }


    override fun onStart() {
        super.onStart()
        val ambulanceId = sharedPref.getString(AMBULANCEID_PREFS, "") as String
        if (ambulanceId.isNullOrBlank()) {
            print("Inside ambulance login")
            val authIntent = Intent(this, AmbulaceLogin::class.java)
            startActivity(authIntent)
            return
        }
        print("validation working")

    }

}
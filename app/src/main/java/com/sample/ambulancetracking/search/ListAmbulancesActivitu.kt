package com.sample.ambulancetracking.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityListAmbulancesActivituBinding
import com.sample.ambulancetracking.journey.AmbulanceAdapter
import com.sample.ambulancetracking.journey.UserRequestAdapter
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.ambulancetracking.retrofit.GetNearbyAmbulancesResponse
import com.sample.ambulancetracking.retrofit.GetNearbyambulancePayload
import com.sample.ambulancetracking.retrofit.GetRequestsByUserResposne
import com.sample.ambulancetracking.tracking.UserLocationTracking
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ListAmbulancesActivitu : AppCompatActivity() {
    private lateinit var binding:ActivityListAmbulancesActivituBinding
    private lateinit  var linearLayoutManager:LinearLayoutManager
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(AmbulanceService::class.java)
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListAmbulancesActivituBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(USER_SECRET,Context.MODE_PRIVATE)
        linearLayoutManager = LinearLayoutManager(this)
        binding.ambulancesListRecyclerView.layoutManager = linearLayoutManager
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                lateinit var response: GetNearbyAmbulancesResponse
                val district:String= intent.getStringExtra("district") as String

                val type:String = intent.getStringExtra("type") as String
                withContext(Dispatchers.IO)
                {
                    response = service.getNearbyAmbulances(GetNearbyambulancePayload(district,type))
                }

                if (!response.areAmbulancesAvailable) {
                    binding.root.snackbar(_500, DISMISS)
                } else {
                    var ambulances = response.ambulances
                    binding.ambulancesListRecyclerView.adapter = AmbulanceAdapter(ambulances)

                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.root.snackbar(_500, DISMISS)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "") as String
        if (userId.isNullOrBlank()) {
            val authIntent = Intent(binding.root.context, AuthActivity::class.java)
            startActivity(authIntent)
        }

    }
}
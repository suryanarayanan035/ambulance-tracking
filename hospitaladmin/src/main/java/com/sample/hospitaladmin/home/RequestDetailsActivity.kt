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
import com.sample.hospitaladmin.databinding.ActivityRequestDetailsBinding
import com.sample.hospitaladmin.home.models.RequestDetailsResponse
import com.sample.hospitaladmin.home.models.UpdateRequestDetails
import com.sample.hospitaladmin.home.models.UpdateRequestDetailsPayload
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlin.Exception

class RequestDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(
        HospitalService::class.java
    )
    private lateinit var binding: ActivityRequestDetailsBinding
    private lateinit var map: GoogleMap
    private lateinit var sharedPref: SharedPreferences
    private val permissionAccepted: Boolean
        get() = checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    private val locationEnabled: Boolean
        get() = try {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        } catch (e: Exception) {
            false
        }
    private var locationManager: LocationManager? = null

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 701
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.requestDetailsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)
        val requestId = "60a80bb3405a9a096d19c49d"
        binding.acceptButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val updateResponse = hospitalService.updateRequestStatus(
                        UpdateRequestDetailsPayload(UpdateRequestDetails(
                            requestId,
                            ACCEPTED
                        ))
                    )
                    if (updateResponse.hasError) {
                        withContext(Dispatchers.Main) {
                            binding.root.snackbar(_500, DISMISS)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            val listIntent = Intent(
                                binding.root.context,
                                ListPendingRequestsHospital::class.java
                            )
                            startActivity(listIntent)

                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        binding.root.snackbar(_500, DISMISS)
                    }
                }


            }
        }
        binding.rejectButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val updateResponse = hospitalService.updateRequestStatus(
                        UpdateRequestDetailsPayload(
                            UpdateRequestDetails(
                            requestId,
                            REJECTED)
                        )
                    )
                    if (updateResponse.hasError) {
                        withContext(Dispatchers.Main) {
                            binding.root.snackbar(_500, DISMISS)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            val listIntent = Intent(
                                binding.root.context,
                                ListPendingRequestsHospital::class.java
                            )
                            startActivity(listIntent)

                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        binding.root.snackbar(_500, DISMISS)
                    }
                }


            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val requestId = "60a80bb3405a9a096d19c49d"
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                var requestDetailsResponse: RequestDetailsResponse
                withContext(Dispatchers.IO) {
                    requestDetailsResponse = hospitalService.getRequestDetails(requestId)
                }

                if (!requestDetailsResponse.isRequestFound) {
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                } else {
                    binding.root.visibility = View.VISIBLE
                    println(requestDetailsResponse)
                    withContext(Dispatchers.Main) {
                        with(requestDetailsResponse)
                        {
                            binding.nameValue.text = name
                            binding.ageValue.text = age.toString()
                            binding.bloodGroupValue.text = bloodGroup
                            binding.mobileValue.text = requestedBy
                            binding.accidentValue.text = if (isAccident) "Yes" else "No"
                            binding.genderValue.text = gender
                            binding.driverNameValue.text = driverName
                            binding.driverMobileValue.text = driverMobile
                            val latitude = location.coordinates[1]
                            val longitude = location.coordinates[0]
                            map.moveCamera((CameraUpdateFactory.zoomTo(20.0f)))
                            map.addMarker(
                                MarkerOptions().position(LatLng(latitude, longitude))
                                    .title("Victim")
                            )
                            map.moveCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        latitude,
                                        longitude
                                    )
                                )
                            )


                        }
                    }


                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val hospitalId = sharedPref.getString(HOSPITALID_PREFS, "")
        if (hospitalId.isNullOrBlank()) {
            val authIntent = Intent(this, HospitalLogin::class.java)
            startActivity(authIntent)
            return
        }
        if (!locationEnabled) {
            Snackbar.make(binding.root, "Location is not enabled", Snackbar.LENGTH_INDEFINITE)
                .setAction("Enable") {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
        if (!permissionAccepted) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

}
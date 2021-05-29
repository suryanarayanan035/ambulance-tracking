package com.sample.ambulancetracking.tracking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityLocationBinding
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.ambulancetracking.retrofit.GetLocationUpdatesResponse
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var sharedPref: SharedPreferences
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var service: AmbulanceService = retrofit.create(AmbulanceService::class.java)
    private lateinit var map: GoogleMap
    private val permissionAccepted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    private val locationEnabled: Boolean
        get() = try {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            false
        }
    private lateinit var locationManager: LocationManager
    private lateinit var currentLocationMarker: Marker
    private lateinit var victimLocationMarker: Marker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        sharedPref = getSharedPreferences(USER_SECRET, Context.MODE_PRIVATE)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.journeyMap) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val requestId = intent.getStringExtra("requestId") as String
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val locationUpdateResponse = service.getLocationUpdates(requestId)
                print(locationUpdateResponse)
                if (locationUpdateResponse.hasError) {

                    withContext(Dispatchers.Main) {
                        binding.root.visibility = View.VISIBLE
                        binding.root.snackbar(_500, DISMISS)

                    }
                    return@launch
                } else {
                    val currentJourneyStatus = locationUpdateResponse.locationUpdate.journeyStatus
                    withContext(Dispatchers.Main) {
                        val ambulanceResponse = locationUpdateResponse.ambulanceDetails
                        binding.journeyStatusValue.text =
                            locationUpdateResponse.locationUpdate.journeyStatus
                        binding.requestStatusValue.text =
                            locationUpdateResponse.locationUpdate.requestStatus
                        binding.driverNameValue.text = ambulanceResponse.driverName
                        binding.driverMobileValue.text = ambulanceResponse.driverMobile
                        binding.hospitalMobileValue.text = ambulanceResponse.hospital
                        binding.vehicleNoValue.text = ambulanceResponse.vehicleNo


                        val ambulanceLocationIndex =
                            locationUpdateResponse.locationUpdate.currentLocation.size - 1
                        val currentLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[1],
                            locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[0]
                        )
                        val victimLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.location.coordinates[1],
                            locationUpdateResponse.locationUpdate.location.coordinates[0]
                        )

                        currentLocationMarker = map.addMarker(
                            MarkerOptions().position(currentLocationLatLng).icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                            )
                        )
                        victimLocationMarker = map.addMarker(
                            MarkerOptions().position(victimLocationLatLng).icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                        )
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
                        map.moveCamera(CameraUpdateFactory.newLatLng(victimLocationLatLng))
                        map.addPolyline(
                            PolylineOptions().add(
                                victimLocationMarker.position,
                                currentLocationMarker.position
                            )
                        )
                        binding.root.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        val locationUpdateResponse: GetLocationUpdatesResponse

                        withContext(Dispatchers.IO) {
                            locationUpdateResponse = service.getLocationUpdates(requestId)
                        }
                        print(locationUpdateResponse)
                        if (locationUpdateResponse.hasError) {

                            withContext(Dispatchers.Main) {
                                binding.root.visibility = View.VISIBLE
                                binding.root.snackbar(_500, DISMISS)

                            }
                            return@launch
                        } else {
                            val ambulanceResponse = locationUpdateResponse.ambulanceDetails
                            binding.journeyStatusValue.text =
                                locationUpdateResponse.locationUpdate.journeyStatus
                            binding.requestStatusValue.text =
                                locationUpdateResponse.locationUpdate.requestStatus
                            binding.driverNameValue.text = ambulanceResponse.driverName
                            binding.driverMobileValue.text = ambulanceResponse.driverMobile
                            binding.hospitalMobileValue.text = ambulanceResponse.hospital
                            binding.vehicleNoValue.text = ambulanceResponse.vehicleNo


                            val ambulanceLocationIndex =
                                locationUpdateResponse.locationUpdate.currentLocation.size - 1
                            val currentLocationLatLng = LatLng(
                                locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[1],
                                locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[0]
                            )
                            val victimLocationLatLng = LatLng(
                                locationUpdateResponse.locationUpdate.location.coordinates[1],
                                locationUpdateResponse.locationUpdate.location.coordinates[0]
                            )

                            currentLocationMarker.position = currentLocationLatLng
                            victimLocationMarker.position = victimLocationLatLng
                            map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
                            map.moveCamera(CameraUpdateFactory.newLatLng(victimLocationLatLng))
                            map.addPolyline(
                                PolylineOptions().add(
                                    victimLocationMarker.position,
                                    currentLocationMarker.position
                                )
                            )
                            binding.root.visibility = View.VISIBLE

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "") as String
        if (userId.isNullOrBlank()) {
            val authIntent = Intent(binding.root.context, AuthActivity::class.java)
            startActivity(authIntent)
        }
        if (!locationEnabled) {
            Snackbar.make(binding.root, "Location is not enabled", Snackbar.LENGTH_SHORT)
                .setAction("Enable") {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
        if (!permissionAccepted) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                UserLocationTracking.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }



    }
}
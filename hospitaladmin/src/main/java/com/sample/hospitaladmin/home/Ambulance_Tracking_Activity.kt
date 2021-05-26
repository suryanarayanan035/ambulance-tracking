package com.sample.hospitaladmin.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.sample.common.*
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityAmbulanceTrackingBinding
import com.sample.hospitaladmin.home.models.*
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class Ambulance_Tracking_Activity : AppCompatActivity(), OnMapReadyCallback {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    lateinit var binding: ActivityAmbulanceTrackingBinding
    lateinit var map: GoogleMap
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private lateinit var currentLocationMarker:Marker
    private lateinit var destinationMarker:Marker
    private lateinit var timer:CountDownTimer
    private val permissionAccepted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityAmbulanceTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.requestDetailsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)
        binding.journeyUpdateButton.setOnClickListener {
            val currentStep = binding.journeyUpdateButton.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val requestId = "60a80bb3405a9a096d19c49d"
                    val ambulanceId = "9361213912"
                    print("Current step$currentStep")
                    var nextStep = getNextStep(currentStep)
                    val updateJourneyStatusResponse = hospitalService.updateJourneyStatus(
                        UpdateJourneyDetailsPayload(
                            UpdateJourneyDetails(
                                requestId,
                                ambulanceId,
                                currentStep
                            )
                        )
                    )
                    if (updateJourneyStatusResponse.hasError) {
                        withContext(Dispatchers.Main) {
                            binding.root.snackbar(_500, DISMISS)
                        }
                        return@launch
                    } else {
                        withContext(Dispatchers.Main) {
                            if (nextStep == "") {
                                val homeIntent =
                                    Intent(binding.root.context, HomeActivity::class.java)
                                startActivity(homeIntent)

                            } else {
                                binding.journeyUpdateButton.text = nextStep
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@launch
                }
            }
        }
    }

    private fun getNextStep(currentStep: String): String {
        if (currentStep == "Not Started") {
            return "On the way"
        }
        if (currentStep == "On the way") {
            return "Arrived at location"
        }
        if (currentStep == "Arrived at location") {
            return "Ride Completed"
        }
        return "";
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val requestId = "60a80bb3405a9a096d19c49d"
        val ambulanceId = "9361213912"
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val locationUpdateResponse = hospitalService.getLocationUpdates(requestId)
                print(locationUpdateResponse)
                if (locationUpdateResponse.hasError) {

                    withContext(Dispatchers.Main) {
                        binding.root.visibility = View.VISIBLE
                        binding.root.snackbar(_500, DISMISS)

                    }
                    return@launch
                } else {
                    val currentJourneyStatus = locationUpdateResponse.locationUpdate.journeyStatus
                    val nextStep = getNextStep(currentJourneyStatus)
                    withContext(Dispatchers.Main) {
                        binding.root.visibility = View.VISIBLE
                        binding.journeyUpdateButton.text = nextStep

                        val currentLocationIndex =
                            locationUpdateResponse.locationUpdate.currentLocation.size - 1
                        val currentLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.currentLocation[currentLocationIndex].coordinates[1],
                            locationUpdateResponse.locationUpdate.currentLocation[currentLocationIndex].coordinates[0])
                        val victimLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.location.coordinates[1],
                            locationUpdateResponse.locationUpdate.location.coordinates[0]
                        )
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
                        currentLocationMarker=map.addMarker(MarkerOptions().position(currentLocationLatLng).title("You")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                        destinationMarker=map.addMarker(MarkerOptions().position(victimLocationLatLng).title("Victim")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        map.addPolyline(
                            PolylineOptions().add(
                                currentLocationLatLng,
                                victimLocationLatLng
                            )
                        )
                        map.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                currentLocationLatLng
                            ))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        timer = object: CountDownTimer(30000,1){
            override fun onTick(millisUntilFinished: Long) {
                print("Location log")
            }
            @SuppressLint("MissingPermission")
            override fun onFinish() {
                try {
                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener {currentLocation:Location->
                        lifecycleScope.launch(Dispatchers.Main)
                        {
                            withContext(Dispatchers.IO) {
                                val updatedLocation=LocationClass("Point",listOf(currentLocation.longitude,currentLocation.latitude))
                                val locationPayload = UpdateLocationPayload(
                                    UpdateLocationDetails(
                                    "60a80bb3405a9a096d19c49d",updatedLocation))
                                val locationUpdateResponse = hospitalService.updateLocation(locationPayload)
                                if(!locationUpdateResponse.isUpdated)
                                {
                                    withContext(Dispatchers.Main)
                                    {
                                        binding.root.snackbar("Cannot update location", DISMISS)
                                    }
                                    return@withContext
                                }
                                currentLocationMarker.position= LatLng(currentLocation.longitude,currentLocation.latitude)
                                withContext(Dispatchers.Main) {
                                    binding.root.snackbar("Location updated", DISMISS)
                                }
                            }
                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.root.snackbar("Exception occured", DISMISS)

                }
            }

        }

        timer.start()

    }



    override fun onStart() {
        super.onStart()

        if (!locationEnabled) {
            Snackbar.make(binding.root, "Location is not enabled", Snackbar.LENGTH_INDEFINITE)
                .setAction("Enable") {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
        if (!permissionAccepted) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RequestDetailsActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }


    }
}


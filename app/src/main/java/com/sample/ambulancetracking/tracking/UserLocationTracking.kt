package com.sample.ambulancetracking.tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.renderscript.RenderScript
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.ActivityUserLocationTrackingBinding
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserLocationTracking : AppCompatActivity(), OnMapReadyCallback {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private lateinit var binding: ActivityUserLocationTrackingBinding
    var service: AmbulanceService = retrofit.create(AmbulanceService::class.java)
    private var map: GoogleMap? = null
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
    private lateinit var sharedPref:SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var youLocation: MutableLiveData<LatLng?> = MutableLiveData(null)
    private var ambulanceLocation: MutableLiveData<LatLng?> = MutableLiveData(null)

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 701
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserLocationTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        sharedPref=getSharedPreferences(USER_SECRET,Context.MODE_PRIVATE)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.requestDetailsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000L
            fastestInterval = 5000L
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                youLocation.value = locationResult.lastLocation.let {
                    LatLng(it.latitude, it.longitude)
                }
            }
        }
        youLocation.observe(this) {
            if (it == null || map == null) return@observe
            map?.clear()
            map?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            ambulanceLocation.value?.let { ambLatLng ->
                map?.addMarker(
                    MarkerOptions()
                        .position(ambLatLng)
                        .title("Ambulance")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
                map?.addPolyline(
                    PolylineOptions().add(
                        it,
                        ambLatLng,
                    )
                )
            }
        }
        ambulanceLocation.observe(this) {
            if (it == null || map == null) return@observe
            map?.clear()
            map?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Ambulance")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            youLocation.value?.let { youLatLng ->
                map?.addMarker(
                    MarkerOptions()
                        .position(youLatLng)
                        .title("You")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                )
                map?.addPolyline(
                    PolylineOptions().add(
                        youLatLng,
                        it,
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
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
                        binding.journeyStatusValue.text=locationUpdateResponse.locationUpdate.journeyStatus
                        binding.requestStatusValue.text=locationUpdateResponse.locationUpdate.requestStatus
                        binding.driverNameValue.text=ambulanceResponse.driverName
                        binding.driverMobileValue.text=ambulanceResponse.driverMobile
                        binding.hospitalMobileValue.text=ambulanceResponse.hospital
                        binding.vehicleNoValue.text=ambulanceResponse.vehicleNo

                        binding.root.visibility = View.VISIBLE
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
                        ambulanceLocation.value = currentLocationLatLng
                        youLocation.value=victimLocationLatLng
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var timer = object:CountDownTimer(120000,1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        val locationUpdateResponse = service.getLocationUpdates(requestId)
                        if(locationUpdateResponse.hasError)
                        {
                            binding.root.snackbar(_500, DISMISS)
                        }
                        else
                        {
                            
                        }
                    }
                    catch(e:Exception) {

                    }

                }
            }

        }


    }



}
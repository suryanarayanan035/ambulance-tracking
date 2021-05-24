package com.sample.ambulancetracking.tracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.ActivityUserLocationTrackingBinding
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.common.BASE_URL
import com.sample.common.DISMISS
import com.sample.common._500
import com.sample.common.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserLocationTracking : AppCompatActivity(),OnMapReadyCallback {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private lateinit var  binding:ActivityUserLocationTrackingBinding
     var service:AmbulanceService = retrofit.create(AmbulanceService::class.java)
    private lateinit var map:GoogleMap
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
        binding = ActivityUserLocationTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.requestDetailsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val requestId = "60a80bb3405a9a096d19c49d"
        val ambulanceId = "9361213912"
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
                        binding.root.visibility = View.VISIBLE
                        val ambulanceLocationIndex =
                            locationUpdateResponse.locationUpdate.currentLocation.size - 1
                        val currentLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[1],
                            locationUpdateResponse.locationUpdate.currentLocation[ambulanceLocationIndex].coordinates[0])
                        val victimLocationLatLng = LatLng(
                            locationUpdateResponse.locationUpdate.location.coordinates[1],
                            locationUpdateResponse.locationUpdate.location.coordinates[0]
                        )
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
                        map.addMarker(
                            MarkerOptions().position(currentLocationLatLng).title("Ambulance")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        map.addMarker(
                            MarkerOptions().position(victimLocationLatLng).title("You")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                        map.addPolyline(
                            PolylineOptions().add(
                                victimLocationLatLng,
                                currentLocationLatLng,

                            )
                        )
                        map.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                victimLocationLatLng
                            ))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                UserLocationTracking.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

}
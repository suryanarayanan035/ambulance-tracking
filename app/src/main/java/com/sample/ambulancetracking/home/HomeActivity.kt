package com.sample.ambulancetracking.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.BuildConfig
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.ActivityHomeBinding
import com.sample.common.USERID_PREFS


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sharedPref: SharedPreferences
    private val permissionAccepted: Boolean
        get() = checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private val locationEnabled: Boolean
        get() = try {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        } catch (e: Exception) {
            false
        }

    private val currentLocationAdded: Boolean
        get() = currentLocationMarker != null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationManager: LocationManager? = null
    private var currentLocationMarker: Marker? = null

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 701
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(
            getString(R.string.prefs_key),
            Context.MODE_PRIVATE
        )

        val userId = sharedPref.getString(USERID_PREFS, "")
        if (!userId.isNullOrBlank()) {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
            return
        }

        binding.searchButton.isEnabled = currentLocationAdded
        locationManager = getSystemService(this, LocationManager::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60000
            fastestInterval = 5000
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                onNewLocation(locationResult.lastLocation)
            }
        }

        binding.searchBody.hospitalRv.adapter = HospitalListAdapter()

        binding.buttonLayout.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.INVISIBLE
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val doubleOffset = slideOffset * 4
                if (doubleOffset > 1) {
//                    binding.currentLocationFab.visibility = View.GONE
                    binding.buttonLayout.visibility = View.INVISIBLE
                    binding.searchLayout.visibility = View.VISIBLE
                    binding.buttonLayout.alpha = 0f
                    binding.searchLayout.alpha = 1f
                } else {
//                    binding.currentLocationFab.visibility = View.VISIBLE
//                    binding.currentLocationFab.alpha = 1 - doubleOffset
                    binding.buttonLayout.visibility = View.VISIBLE
                    binding.searchLayout.visibility = View.VISIBLE
                    binding.buttonLayout.alpha = 1 - doubleOffset
                    binding.searchLayout.alpha = doubleOffset
                }
                when {
                    slideOffset < 0.05 -> {
                        binding.buttonLayout.visibility = View.VISIBLE
                        binding.searchLayout.visibility = View.INVISIBLE
                    }
                }
            }
        })
        binding.searchButton.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        if (!permissionAccepted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            if (!locationEnabled) {
                Snackbar.make(binding.root, "Location is not enabled", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Enable") {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }.show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun onNewLocation(location: Location) {
        currentLocationMarker?.remove()
        val userLatLng = LatLng(location.latitude, location.longitude)
        currentLocationMarker = map.addMarker(MarkerOptions().position(userLatLng))
        map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
        binding.searchButton.isEnabled = currentLocationAdded
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                when {
                    grantResults.isEmpty() -> {
                        Snackbar.make(
                            binding.root,
                            "Permission is not granted",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("Request") {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                                )
                            }.show()
                    }
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        requestLocationUpdates()
                    }
                    else -> {
                        Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Request Permission") {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri: Uri = Uri.fromParts(
                                    "package",
                                    BuildConfig.APPLICATION_ID, null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .show()
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (permissionAccepted) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun removeLocationUpdates() {
        if (permissionAccepted) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "")
        if (!userId.isNullOrBlank()) {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
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
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
        requestLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        removeLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
//        map.addMarker()
    }

}
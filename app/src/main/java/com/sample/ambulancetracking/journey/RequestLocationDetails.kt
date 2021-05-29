package com.sample.ambulancetracking.journey

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityRequestLocationDetailsBinding
import com.sample.ambulancetracking.retrofit.*
import com.sample.ambulancetracking.tracking.UserLocationTracking
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestLocationDetails : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var map:GoogleMap
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    var service= retrofit.create(AmbulanceService::class.java)
    private lateinit var binding:ActivityRequestLocationDetailsBinding
    private lateinit var sharedPref:SharedPreferences
    private val permissionAccepted:Boolean get() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    private val locationEnabled:Boolean get() = try {
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }catch(e:Exception){
        false
    }
    private var locationManager:LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker:Marker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestLocationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(USER_SECRET, Context.MODE_PRIVATE)
        var mapFragment = supportFragmentManager.findFragmentById(R.id.requestLocationMap) as SupportMapFragment
        if(mapFragment == null)
        {
            mapFragment = SupportMapFragment()
        }
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = ContextCompat.getSystemService(this,LocationManager::class.java)

        binding.createRequestButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {

                    lateinit var response:CreateRequestAndJourneyResponse
                    withContext(Dispatchers.Main) {
                        val requestJourneyDetails:RequestJourneyDetails = intent.getSerializableExtra("requestJourneyBasicDetails") as RequestJourneyDetails
                        requestJourneyDetails.location = LocationPayload("Point", listOf(marker.position.longitude,marker.position.latitude))
                        requestJourneyDetails.requestedBy=sharedPref.getString(USERID_PREFS,"") as String
                        print("Requested By"+sharedPref.getString(USERID_PREFS,"") as String)
                        response = service.createRequestAndJourney(RequestJourneyDetailsPayload(requestJourneyDetails))
                    }
                        if(response.hasError)
                        {
                            withContext(Dispatchers.Main){
                                binding.root.snackbar(_500, DISMISS)
                            }
                        }
                        else
                        {
                            withContext(Dispatchers.Main){
                                val trackingIntent = Intent(binding.root.context,UserLocationTracking::class.java)
                                trackingIntent.putExtra("requestId",response.requestId);
                                startActivity(trackingIntent)
                                finish()
                            }

                        }
                    }

                catch(e:Exception)
                {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        binding.root.snackbar(_500, DISMISS)
                    }

                }

            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap){
     map = googleMap
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,CancellationTokenSource().token).addOnSuccessListener { currentLocation:Location->
        marker = map.addMarker(MarkerOptions().position(LatLng(currentLocation.latitude,currentLocation.longitude)).draggable(true))
        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(currentLocation.latitude,currentLocation.longitude)))
        }
    }

    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "") as String
        if (userId.isNullOrBlank()) {
            val authIntent = Intent(binding.root.context, AuthActivity::class.java)
            startActivity(authIntent)
        }
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
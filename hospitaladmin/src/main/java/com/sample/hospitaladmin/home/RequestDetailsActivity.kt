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
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.snackbar.Snackbar
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapError
import com.here.sdk.mapview.MapScene
import com.here.sdk.mapview.MapScheme
import com.sample.common.BASE_URL
import com.sample.common.HOSPITALADMIN_SECRET
import com.sample.common.HOSPITALID_PREFS
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityRequestDetailsBinding
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.jar.Manifest
import kotlin.Exception

class RequestDetailsActivity : AppCompatActivity(),OnMapReadyCallback {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(
        HospitalService::class.java)
    private lateinit var binding:ActivityRequestDetailsBinding
    private lateinit var map:GoogleMap
    private lateinit var sharedPref:SharedPreferences
    private val permissionAccepted:Boolean get() = checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    private val locationEnabled:Boolean get() = try{
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?:false
    } catch(e:Exception) {
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
        binding.root.visibility=View.INVISIBLE
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        val hospitalId = sharedPref.getString(HOSPITALID_PREFS,"")
        val requestId = "60a80bb3405a9a096d19c49d"
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestDetailsResponse = hospitalService.getRequestDetails(requestId)
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }

        
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onStart() {
        super.onStart()
        val hospitalId = sharedPref.getString(HOSPITALID_PREFS,"")
        if(hospitalId.isNullOrBlank())
        {
            val authIntent = Intent(this,HospitalLogin::class.java)
            startActivity(authIntent)
            return
        }
        if(!locationEnabled) {
            Snackbar.make(binding.root,"Location is not enabled",Snackbar.LENGTH_INDEFINITE)
                .setAction("Enable") {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
        if(!permissionAccepted) {
            ActivityCompat.requestPermissions(
                this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

}
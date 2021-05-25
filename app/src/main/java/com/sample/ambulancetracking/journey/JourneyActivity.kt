package com.sample.ambulancetracking.journey

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.BuildConfig
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityJourneyBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.ambulancetracking.retrofit.*
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JourneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJourneyBinding
    private lateinit var phoneNumber: String
    private lateinit var name: String
    private lateinit var password: String
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val ambulanceService = retrofit.create(AmbulanceService::class.java)
    private lateinit var sharedPref: SharedPreferences
    private var locationManager: LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionAccepted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private val locationEnabled: Boolean
        get() = try {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        } catch (e: Exception) {
            false
        }

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var youLocation: LatLng? = null

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 701
        const val TAG = "HomeActivity"
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val extraPhoneNumber = it.getStringExtra(EXTRA_MOBILE) ?: run {
                finish()
                return
            }
            val extraName = it.getStringExtra(EXTRA_NAME) ?: run {
                finish()
                return
            }
            val extraPassword = it.getStringExtra(EXTRA_PASSWORD) ?: run {
                finish()
                return
            }
            phoneNumber = extraPhoneNumber
            name = extraName
            password = extraPassword
        } ?: run {
            finish()
            return
        }

        sharedPref = getSharedPreferences(
            getString(R.string.prefs_key),
            Context.MODE_PRIVATE
        )

        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000L
            fastestInterval = 5000L
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                youLocation = locationResult.lastLocation.let {
                    println(it.latitude)
                    LatLng(it.latitude, it.longitude)
                }

            }
        }

        if (!locationEnabled) {
            binding.root.snackbar(
                msg = "Location is not enabled",
                action = "Enable"
            ) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

        binding.userNameText.text = name

        binding.bloodGroupInput.setAdapter(
            ArrayAdapter(
                this, R.layout.layout_blood_item,
                BloodGroup.values().map { it.blood }
            )
        )

        binding.genderInput.setAdapter(
            ArrayAdapter(
                this, R.layout.layout_blood_item,
                Gender.values().map { it.gender }
            )
        )

        binding.button.setOnClickListener {
            val age = binding.ageInput.text.toString().trim()
            val bloodGroup = binding.bloodGroupInput.text.toString().trim()
            val gender = binding.genderInput.text.toString()
            val address = binding.addrLineInput.text.toString().trim()
            val district = binding.districtInput.text.toString().trim()
            val pincode = binding.pincodeInput.text.toString().trim()
            println("$age, $bloodGroup, $gender, $address, $district, $pincode")
            val validated = validateUserPayload(
                name = name,
                mobile = phoneNumber,
                age = age,
                gender = gender,
                bloodGroup = bloodGroup,
                street = address,
                district = district,
                pincode = pincode,
                password = password
            )
            if (!validated) {
                binding.root.snackbar("Invalid format", "Dismiss")
                return@setOnClickListener
            }
            if (!permissionAccepted) {
                binding.root.snackbar("Permission is not accepted", "Request") {
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
                return@setOnClickListener
            }
            btnLoading()
            val location = youLocation
            if (location != null) {

                val payload = RegisterPayload(
                    user = UserPayload(
                        name = name,
                        mobile = phoneNumber,
                        age = age.toInt(),
                        gender = Gender.valueOf(gender),
                        bloodGroup = BloodGroup.valueOf(bloodGroup),
                        address = AddressPayload(
                            street = address,
                            district = district,
                            pincode = pincode,
                        ),
                        location = LocationPayload(
                            type = "Point",
                            coordinates = listOf(location.latitude, location.longitude)
                        ),
                        password = password,
                    )
                )
                signup(payload)

            } else {
                btnIdle()
                binding.root.snackbar("Location not found", "Request")
            }

        }

    }


    private fun btnLoading() {
        binding.button.isEnabled = false
    }

    private fun btnIdle() {
        binding.button.isEnabled = true
    }

    private fun signup(payload: RegisterPayload) {
        btnLoading()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val checkResponse = ambulanceService.check(phoneNumber)
                if (!checkResponse.isUserExists) {
                    withContext(Dispatchers.Main) {
                        btnIdle()
                        val authIntent = Intent(this@JourneyActivity, AuthActivity::class.java)
                        startActivity(authIntent)
                        finish()
                    }
                    return@launch
                }
                val registerResponse = ambulanceService.signUp(
                    payload = payload
                )
                if (!registerResponse.isValid) {
                    withContext(Dispatchers.Main) {
                        btnIdle()
                        binding.root.snackbar(
                            "Please login",
                            "Dismiss",
                        )
                    }
                    return@launch
                }

                with(sharedPref.edit()) {
                    putString(USERID_PREFS, checkResponse.user.mobile.trim())
                    commit()
                }
                withContext(Dispatchers.Main) {
                    btnIdle()
                    val homeIntent = Intent(this@JourneyActivity, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnIdle()
                    binding.root.snackbar(e.message, "Dismiss")
                }
            }
        }
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
                        binding.root.snackbar(
                            msg = "Permission is not granted",
                            action = "Request"
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                            )
                        }
                    }
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.root.snackbar(
                            msg = "Permission denied",
                            action = "Request Permission"
                        ) {
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
                    }
                }
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
}
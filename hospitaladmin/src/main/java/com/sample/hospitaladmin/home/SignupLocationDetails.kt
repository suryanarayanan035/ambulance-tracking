package com.sample.hospitaladmin.home

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.common.BASE_URL
import com.sample.hospitaladmin.databinding.ActivitySignupLocationDetailsBinding
import com.sample.hospitaladmin.retrofit.HospitalService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks.await
import com.sample.common.snackbar
import com.sample.hospitaladmin.home.models.Address
import com.sample.hospitaladmin.home.models.Hospital
import com.sample.hospitaladmin.home.models.HospitalSignUpPayload
import com.sample.hospitaladmin.home.models.LocationClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupLocationDetails : AppCompatActivity() {
    /** declaring retrofit properties for connecting to backend*/
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    /** creating bing propery for a=layout activities*/
    lateinit var binding: ActivitySignupLocationDetailsBinding
    /** creating object of FusedLocationProviderClient for getting location details*/
    lateinit var locationClient:FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupLocationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        val service:LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted:Boolean->
                if(isGranted) {
                    if(!service.isProviderEnabled(this.toString()))
                    {
                        AlertDialog.Builder(this).setTitle("GPS Required")
                            .setMessage("Please Turn On GPS")
                            .setPositiveButton("Yes",null)
                            .show()
                    }
                }
            else  { }
        }
        when(PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission (
                this,Manifest.permission.ACCESS_FINE_LOCATION
                    )->{}
            else->{requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)}
        }
        binding.signUpButton.setOnClickListener{
            val street = binding.street.text.toString()
            val district = binding.district.text.toString()
            val pincode = binding.pincode.text.toString()
            val validationMessage = validateAddressDetails(street,district,pincode)
            val cancellationToken = CancellationTokenSource().token
            var hospital = intent.getSerializableExtra("hospital") as Hospital
            if(validationMessage=="")
            {
                hospital.address = Address(street,district,pincode)
                locationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken
                ).addOnSuccessListener { location:Location->
                    hospital.location = LocationClass(type="Point",coordinates = listOf(location.longitude,location.latitude))
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val saveHospitalResponse = hospitalService.signUpHospital(
                                HospitalSignUpPayload(hospital)
                            )
                            if(saveHospitalResponse.hasError)
                            {
                                withContext(Dispatchers.Main) {
                                    binding.root.snackbar("Error Occures :(","DISMISS")
                                }

                            }
                            else
                            {
                                withContext(Dispatchers.Main)
                                {
                                    val homeIntent = Intent(binding.signUpButton.context,ListPendingRequestsHospital::class.java)
                                    startActivity(homeIntent)
                                }
                            }
                        }
                        catch(e:Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.IO) {
                                binding.root.snackbar("Connection error occured","DISMISS")
                            }
                        }
                    }
                }

            }
            else
            {
                binding.root.snackbar(validationMessage,"DISMISS")
            }
        }
    }
    private  fun validateAddressDetails(street:String,district:String,pincode:String):String{
        if(street.length < 3)
        {
            return "street must be atleast three chars"
        }
        if(district.length < 3)
        {
            return "street must be atleast three chars"
        }
        if(pincode.length < 3)
        {
            return "street must be atleast three chars"
        }

        return ""
    }
}
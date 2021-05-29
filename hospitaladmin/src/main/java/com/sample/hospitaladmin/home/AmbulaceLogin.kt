package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sample.common.*
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityAmbulaceLoginBinding
import com.sample.hospitaladmin.home.models.AmbulanceLoginDetails
import com.sample.hospitaladmin.home.models.AmbulanceLoginPayload
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class AmbulaceLogin : AppCompatActivity() {
    private lateinit var binding:ActivityAmbulaceLoginBinding
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmbulaceLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        binding.ambulanceLoginButton.setOnClickListener{
            val ambulanceId = binding.ambulanceID.text.toString()
            val password = binding.password.text.toString()
            val validationMessage = validateAmbulanceLoginDetails(ambulanceId,password)
            if(validationMessage == "")
            {
                val loginPayload = AmbulanceLoginPayload(AmbulanceLoginDetails(ambulanceId,password))
                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val ambulanceLoginResponse = hospitalService.ambulanceLogin(loginPayload)
                        if(!ambulanceLoginResponse.isValid)
                        {
                            withContext(Dispatchers.Main) {
                                binding.root.snackbar("Invalid username/password","DISMISS")
                            }
                            return@launch
                        }
                        sharedPref.edit().putString(AMBULANCEID_PREFS,ambulanceId).commit()
                        val ambulanceHomeIntent = Intent(binding.root.context,Ambulance_Home::class.java)
                        startActivity(ambulanceHomeIntent)
                    }
                }
                catch(e:Exception) {
                    e.printStackTrace()
                    binding.root.snackbar("Internal server error","DISMISS")
                }
            }
            else
            {
                binding.root.snackbar(validationMessage,"DISMISS")
            }
        }
    }
    private fun validateAmbulanceLoginDetails(ambulanceId:String,password:String):String {
        if(ambulanceId.length != 10)
        {
            return "ambulanceId/mobileNo should be equal to 10 cahrs"
        }
        if(password.length < 8)
        {
            return "password should be atleast 8 chars"
        }
        return ""
    }
    override fun onStart() {
        super.onStart()
        val ambulanceId = sharedPref.getString(AMBULANCEID_PREFS,"")

        if(!ambulanceId.isNullOrBlank())
        {
            val hospitalHomeIntent = Intent(this,Ambulance_Home::class.java)
            startActivity(hospitalHomeIntent)
            return
        }
    }
}
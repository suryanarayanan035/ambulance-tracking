package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sample.common.*
import com.sample.hospitaladmin.databinding.ActivityHospitalLoginBinding
import com.sample.hospitaladmin.home.models.HospitalLoginDetails
import com.sample.hospitaladmin.home.models.HospitalLoginPayload
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sign

class HospitalLogin : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalLoginBinding
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalLoginBinding.inflate(layoutInflater)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET,Context.MODE_PRIVATE)
        setContentView(binding.root)
        binding.signUp.setOnClickListener{
            val signUpIntent = Intent(this,SignupActivity::class.java)
            startActivity(signUpIntent)
        }
        binding.hospitalLoginButton.setOnClickListener{
            val hospitalId = binding.hospitalId.text.toString()
            val password = binding.hospitalLoginPassword.text.toString()
            val validationMessage = validateHospitalLoginDetails(hospitalId,password)
            sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
            if(validationMessage == "")
            {
                val loginPayload = HospitalLoginPayload(HospitalLoginDetails(hospitalId,password))
                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val hospitalLoginResponse = hospitalService.hospitalLogin(loginPayload)
                        if(!hospitalLoginResponse.isValid)
                        {
                            withContext(Dispatchers.Main) {
                                binding.root.snackbar("Invalid username/password","DISMISS")
                            }

                        }
                        else
                        {
                            sharedPref.edit().putString(HOSPITALID_PREFS,hospitalId).commit()

                            withContext(Dispatchers.Main){
                                val hospitalHomeIntent = Intent(binding.root.context, HospitalHomeScreen::class.java)
                                startActivity(hospitalHomeIntent)
                            }
                        }
                    }
                }
                catch(e:Exception) {
                    e.printStackTrace()
                    binding.root.snackbar(_500,"DISMISS")
                }
            }
            else
            {
                binding.root.snackbar(validationMessage,"DISMISS")
            }
        }
    }

    private fun validateHospitalLoginDetails(hospitalId:String,password:String):String {
        if(hospitalId.length != 10)
        {
            return "hospitalId/mobileNo should be equal to 10 chars"
        }
        if(password.length < 8)
        {
            return "password should be atleast 8 chars"
        }
        return ""
    }

    override fun onStart() {
        super.onStart()
        val hospitalId = sharedPref.getString(HOSPITALID_PREFS,"")

        if(!hospitalId.isNullOrBlank())
        {
            val hospitalHomeIntent = Intent(this,HospitalHomeScreen::class.java)
            startActivity(hospitalHomeIntent)

        }
    }
}
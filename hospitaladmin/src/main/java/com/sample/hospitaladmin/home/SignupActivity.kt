package com.sample.hospitaladmin.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import com.sample.common.BASE_URL
import com.sample.common.snackbar
import com.sample.hospitaladmin.databinding.ActivitySignupBinding
import com.sample.hospitaladmin.home.models.Hospital
import com.sample.hospitaladmin.retrofit.HospitalService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    lateinit var binding:ActivitySignupBinding
     var hospital:Hospital = Hospital()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.proceedButton.setOnClickListener {
            var name = binding.name.text.toString()
            var mobile = binding.mobile.text.toString()
            val password = binding.password.text.toString()
            print("password $password")
            val type =findViewById<RadioButton>(binding.radioGroup.checkedRadioButtonId).text.toString();
            val validationMessage = validateHospitalSignupData(name, mobile, password, type)
            if (validationMessage == "") {
                hospital.name=name
                hospital.password=password
                hospital.type=type
                hospital.mobile = mobile

                val locationIntent = Intent(this,SignupLocationDetails::class.java)
                locationIntent.putExtra("hospital",hospital)
                startActivity(locationIntent)

            } else {
                 binding.root.snackbar(validationMessage,"Dismiss")
        }
        }
    }

    private fun validateHospitalSignupData(name:String, mobile:String, password:String, type:String):String {
        if(name.length < 3)
        {
            return "Name must be greater than 3 chars"
        }
        if(mobile.length !=10)
        {
            return "Mobile number must be exactly 10 digits"
        }
        if(password.length < 8)
        {
            return "password must be greater than 8 digits"
        }
        if(type != "Private" && type!="Government")
        {
            return "Invalid Type"
        }
        return ""
    }
}
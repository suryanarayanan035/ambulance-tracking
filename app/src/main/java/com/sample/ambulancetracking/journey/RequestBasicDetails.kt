package com.sample.ambulancetracking.journey

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityRequestBasicDetailsBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.ambulancetracking.retrofit.RequestJourneyDetails
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class RequestBasicDetails : AppCompatActivity() {
    private lateinit var binding:ActivityRequestBasicDetailsBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
      sharedPref =getSharedPreferences(USER_SECRET, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBasicDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.maleButton.isSelected = true

        val spinner: Spinner = binding.bloodGroupSpinner
        ArrayAdapter.createFromResource(this,
            R.array.blood_group_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=adapter
        }
        binding.nextButton.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main) {
            try {
                val name = binding.name.text.toString()
                val age = binding.age.text.toString().toInt()
                val gender = findViewById<RadioButton>(binding.gender.checkedRadioButtonId).text.toString()
                val bloodGroup = binding.bloodGroupSpinner.selectedItem.toString()
                val isAccident = binding.isAccidentSwitch.isSelected
                val hospital=intent.getStringExtra("hospitalId") as String
                val ambulance=intent.getStringExtra("ambulanceId") as String
                val requestedBy = sharedPref.getString(USERID_PREFS,"") as String
                val validationMessage = validateBasicDetails(name,age,gender,bloodGroup)
                if(validationMessage != "")
                {
                    withContext(Dispatchers.Main) {
                        binding.root.snackbar(validationMessage, DISMISS)
                    }
                    return@launch
                }

                val requestJourneyBasicDetails:Serializable = RequestJourneyDetails(name,age,bloodGroup,gender,requestedBy, hospital, ambulance, isAccident)
                withContext(Dispatchers.Main) {
                    val intent = Intent(binding.root.context,RequestLocationDetails::class.java)
                    intent.putExtra("requestJourneyBasicDetails",requestJourneyBasicDetails)
                    startActivity(intent)
                   finish()

                }

            }
            catch(e:Exception)
            {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.root.snackbar("Exception occured", DISMISS)
                }
            }

            }
        }
    }

    private fun validateBasicDetails(name:String,age:Int,gender:String,bloodGroup:String):String {
        if(name?.length < 3)
        {
            return "name should be atleast three chars"
        }
        if(age < 0)
        {
            return "age should be minimum 0";
        }
        if(gender != "Male" && gender !="Female" && gender !="Transgender")
        {
            return "please select gender"
        }
        if(bloodGroup?.length < 3)
        {
            return "please selct a blood group"
        }
        return ""
    }

    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "") as String
        if (userId.isNullOrBlank()) {
            val authIntent = Intent(binding.root.context, AuthActivity::class.java)
            startActivity(authIntent)
        }
    }
    }



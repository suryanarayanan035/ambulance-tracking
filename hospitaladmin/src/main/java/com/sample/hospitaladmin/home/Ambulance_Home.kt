package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.common.AMBULANCEID_PREFS
import com.sample.common.HOSPITALADMIN_SECRET
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityAmbulanceHomeBinding

class Ambulance_Home : AppCompatActivity() {
    private lateinit var binding:ActivityAmbulanceHomeBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmbulanceHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        binding.ambulanceLogOutButton.setOnClickListener{
            sharedPref.edit().remove(AMBULANCEID_PREFS).commit()
            val selectionIntent = Intent(binding.root.context,SelectionScreen::class.java)
            startActivity(selectionIntent)
            finish()
        }
        binding.victimDetailsButton.setOnClickListener{
            val victimDetailsIntent = Intent(binding.root.context,VictimDetails::class.java)
            startActivity(victimDetailsIntent)
        }
        binding.locationDetailsButton.setOnClickListener{
            val victimDetailsIntent = Intent(binding.root.context,Ambulance_Tracking_Activity::class.java)
            startActivity(victimDetailsIntent)
        }
    }
}
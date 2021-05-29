package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.common.AMBULANCEID_PREFS
import com.sample.common.HOSPITALADMIN_SECRET
import com.sample.common.HOSPITALID_PREFS
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivitySelectionScreenBinding

class SelectionScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySelectionScreenBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        binding = ActivitySelectionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.hospitalAdminButton.setOnClickListener{
            val hospitalIntent = Intent(this,HospitalLogin::class.java)
            startActivity(hospitalIntent)
        }
        binding.ambulanceDriverButton.setOnClickListener{
            val ambulanceIntent = Intent(this,AmbulaceLogin::class.java)
            startActivity(ambulanceIntent)
        }
    }


}

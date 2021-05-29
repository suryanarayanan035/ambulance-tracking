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
import com.sample.hospitaladmin.databinding.ActivityHospitalHomeScreenBinding

class HospitalHomeScreen : AppCompatActivity() {
    private lateinit var binding:ActivityHospitalHomeScreenBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        binding.showRequestButton.setOnClickListener{
            val requestIntent = Intent(binding.root.context,ListPendingRequestsHospital::class.java)
            startActivity(requestIntent)
        }

        binding.createAmbulanceButton.setOnClickListener{
            val createIntent = Intent(binding.root.context,AmbulanceSignup::class.java)
            startActivity(createIntent)
        }

        binding.hospitalLogoutButton.setOnClickListener{
            sharedPref.edit().remove(HOSPITALID_PREFS).commit()
            val selectionIntent = Intent(binding.root.context,SelectionScreen::class.java)
            startActivity(selectionIntent)
            finish()
        }
    }
}
package com.sample.hospitaladmin.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivitySelectionScreenBinding

class SelectionScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySelectionScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
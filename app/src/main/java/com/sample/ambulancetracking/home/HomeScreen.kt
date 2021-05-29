package com.sample.ambulancetracking.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityHomeScreenBinding
import com.sample.ambulancetracking.journey.ListUserRequests
import com.sample.ambulancetracking.journey.RequestBasicDetails
import com.sample.ambulancetracking.search.ListAmbulancesActivitu
import com.sample.ambulancetracking.search.SearchAmbulanceActivity
import com.sample.common.USERID_PREFS
import com.sample.common.USER_SECRET

class HomeScreen : AppCompatActivity() {
    private lateinit var binding:ActivityHomeScreenBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = getSharedPreferences(USER_SECRET, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listRequestButton.setOnClickListener{
            val listIntent = Intent(binding.root.context,ListUserRequests::class.java)
            startActivity(listIntent)
        }
        binding.ambulanceSearchButton.setOnClickListener{
            val requestIntent = Intent(binding.root.context,SearchAmbulanceActivity::class.java)
            startActivity(requestIntent)
        }
        binding.logoutButton.setOnClickListener{
            sharedPref.edit().remove(USERID_PREFS).commit()
            val authIntent = Intent(binding.root.context,AuthActivity::class.java)
            startActivity(authIntent)
        }
    }
    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS,"") as String
        if(userId.isNullOrBlank())
        {
            val authIntent = Intent(binding.root.context,AuthActivity::class.java)
            startActivity(authIntent)
        }
    }
}
package com.sample.ambulancetracking.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.RadioButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivitySearchAmbulanceBinding
import com.sample.ambulancetracking.tracking.UserLocationTracking
import com.sample.common.DISMISS
import com.sample.common.USERID_PREFS
import com.sample.common.USER_SECRET
import com.sample.common.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchAmbulanceActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchAmbulanceBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySearchAmbulanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.allRadio.isSelected=true
        sharedPref = getSharedPreferences(USER_SECRET,Context.MODE_PRIVATE)
        binding.searchAmbulanceButton.setOnClickListener {
            val district = binding.district.text.toString()
            var type = findViewById<RadioButton>(binding.hospitalTypeGroup.checkedRadioButtonId).text
            type = if(type=="All") "" else type
            if(district.length < 3)
            {
                binding.root.snackbar("District must be atleast 3 chars long", DISMISS)
            }
            else
            {
               val listIntent  = Intent(this,ListAmbulancesActivitu::class.java)
                listIntent.putExtra("district",district)
                listIntent.putExtra("type",type)
                startActivity(listIntent)
            }
        }
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
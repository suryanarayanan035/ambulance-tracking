package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import com.sample.common.*
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityListPendingRequestsHospitalBinding
import com.sample.hospitaladmin.home.models.Request
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListPendingRequestsHospital : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    private lateinit var binding: ActivityListPendingRequestsHospitalBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPendingRequestsHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.INVISIBLE
        sharedPref = getSharedPreferences(HOSPITALADMIN_SECRET,Context.MODE_PRIVATE)
        val hospitalId = sharedPref.getString(HOSPITALID_PREFS,"")

        if(hospitalId.isNullOrBlank()){
            val hospitalAuthIntent = Intent(this,HospitalLogin::class.java)
            startActivity(hospitalAuthIntent)
        }
        else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val requestsResponse = hospitalService.getPendingRequestsByHospital(hospitalId)
                    if (requestsResponse.hasError) {
                        withContext(Dispatchers.Main)
                        {
                            binding.root.snackbar(_500, DISMISS)
                        }

                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        binding.pendingRequestsRv.adapter =
                            HospitalRequestListAdapter(requestsResponse.requests)
                        binding.root.visibility=View.VISIBLE

                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                    withContext(Dispatchers.Main)
                    {
                        binding.root.snackbar(_500, DISMISS)
                    }
                    return@launch

                }

            }

        }


    }
}
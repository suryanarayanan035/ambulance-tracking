package com.sample.ambulancetracking.journey

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.auth.AuthActivity
import com.sample.ambulancetracking.databinding.ActivityListUserRequestsBinding
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.ambulancetracking.retrofit.GetRequestsByUser
import com.sample.ambulancetracking.retrofit.GetRequestsByUserResposne
import com.sample.ambulancetracking.tracking.UserLocationTracking
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ListUserRequests : AppCompatActivity(){
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(AmbulanceService::class.java)
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var requests:List<GetRequestsByUser>
    private lateinit var binding: ActivityListUserRequestsBinding
    private lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListUserRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(USER_SECRET, Context.MODE_PRIVATE)
        linearLayoutManager = LinearLayoutManager(this)
        binding.userRequestRecyclerView.layoutManager = linearLayoutManager
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                lateinit var response: GetRequestsByUserResposne
                withContext(Dispatchers.IO)
                {
                    response = service.getRequestsByUser("8428169669")
                }

                if (response.hasError) {
                    binding.root.snackbar(_500, DISMISS)
                } else {
                    requests = response.requests
                    binding.userRequestRecyclerView.adapter = UserRequestAdapter(requests)

                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.root.snackbar(_500, DISMISS)
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
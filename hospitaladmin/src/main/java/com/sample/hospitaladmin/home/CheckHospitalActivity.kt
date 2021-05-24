package com.sample.hospitaladmin.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sample.common.BASE_URL
import com.sample.common.snackbar
import com.sample.hospitaladmin.databinding.ActivityCheckHospitalBinding
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CheckHospitalActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    lateinit var binding:ActivityCheckHospitalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.checkButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {

                    val response  = hospitalService.check(binding.editTextTextPersonName.text.toString())
                    if(!response.isHospitalExists)
                    {
                        withContext(Dispatchers.Main) {
                            binding.root.snackbar("Hospitals not Exists","Dismiss")
                        }
                    }
                    else
                    {
                        withContext(Dispatchers.Main) {
                            binding.root.snackbar("Hospital Exists","Dismiss")
                        }
                    }

                }
                catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}
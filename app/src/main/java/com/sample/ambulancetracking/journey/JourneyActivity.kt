package com.sample.ambulancetracking.journey

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.ActivityJourneyBinding

class JourneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJourneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bloodGroupInput.setAdapter(
            ArrayAdapter(
                this, R.layout.layout_blood_item,
                listOf(
                    "B +ve",
                    "A +ve",
                    "O +ve",
                    "AB +ve",
                    "A -ve",
                    "B -ve",
                    "O -ve",
                    "AB -ve",
                    "Unknown"
                )
            )
        )

        binding.genderInput.setAdapter(
            ArrayAdapter(
                this, R.layout.layout_blood_item,
                listOf(
                    "Male", "Female", "Transgender"
                )
            )
        )

        binding.button.setOnClickListener {

        }
    }
}
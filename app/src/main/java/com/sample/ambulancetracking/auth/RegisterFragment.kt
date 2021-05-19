package com.sample.ambulancetracking.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.FragmentRegisterBinding
import com.sample.ambulancetracking.journey.JourneyActivity
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterFragment : Fragment() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val ambulanceService = retrofit.create(AmbulanceService::class.java)
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.prefs_key),
            Context.MODE_PRIVATE
        )
        return FragmentRegisterBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentRegisterBinding.bind(view)

        binding.registerButton.setOnClickListener {
            val phoneNumber = binding.registerPhoneNumberInput.text.toString().trim()
            val password = binding.registerPasswordInput.text.toString().trim()
            val name = binding.registerNameInput.text.toString().trim()
            val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
            if (!phoneNumber.isMobile() || !password.isPassword() || !name.isName()) {
                bottomNavView.snackbar("PhoneNumber or Password or Name is not valid", "Dismiss", true)
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

                try {
                    val checkResponse = ambulanceService.check(phoneNumber)
                    if (checkResponse.isUserExists) {
                        withContext(Dispatchers.Main) {
                            bottomNavView.snackbar(
                                "Please login to continue",
                                "Dismiss",
                                true
                            )
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        val journeyIntent = Intent(requireContext(), JourneyActivity::class.java).apply {
                            putExtra(EXTRA_MOBILE, phoneNumber)
                            putExtra(EXTRA_NAME, name)
                            putExtra(EXTRA_PASSWORD, password)
                        }
                        startActivity(journeyIntent)
                        requireActivity().finish()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        bottomNavView.snackbar(e.message, "Dismiss", true)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}
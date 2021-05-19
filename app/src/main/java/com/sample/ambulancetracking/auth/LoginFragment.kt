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
import com.sample.ambulancetracking.databinding.FragmentLoginBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.ambulancetracking.retrofit.LoginDetailsPayload
import com.sample.ambulancetracking.retrofit.LoginPayload
import com.sample.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val ambulanceService = retrofit.create(AmbulanceService::class.java)
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.prefs_key),
            Context.MODE_PRIVATE
        )
        return FragmentLoginBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.loginPhoneNumberInput.text.toString().trim()
            val password = binding.loginPasswordInput.text.toString().trim()
            val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
            if (!phoneNumber.isMobile() || !password.isPassword()) {
                bottomNavView.snackbar("PhoneNumber or Password is not valid", "Dismiss", true)
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

                try {
                    val checkResponse = ambulanceService.check(phoneNumber)
                    if (!checkResponse.isUserExists) {
                        withContext(Dispatchers.Main) {
                            bottomNavView.snackbar(
                                "Not a valid id or password",
                                "Dismiss",
                                true
                            )
                        }
                        return@launch
                    }
                    val loginResponse = ambulanceService.login(
                        payload = LoginPayload(
                            loginDetails = LoginDetailsPayload(
                                userId = phoneNumber,
                                password = password,
                            )
                        )
                    )
                    if (!loginResponse.isValid) {
                        withContext(Dispatchers.Main) {
                            bottomNavView.snackbar(
                                "Please register yourself before logging in",
                                "Dismiss",
                                true
                            )
                        }
                        return@launch
                    }

                    with(sharedPref.edit()) {
                        putString(USERID_PREFS, checkResponse.user.mobile.trim())
                        commit()
                    }
                    withContext(Dispatchers.Main) {
                        val homeIntent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(homeIntent)
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
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}
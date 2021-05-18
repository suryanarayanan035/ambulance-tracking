package com.sample.ambulancetracking.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.FragmentLoginBinding
import com.sample.ambulancetracking.retrofit.AmbulanceService
import com.sample.common.isMobile
import com.sample.common.isPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private val retrofit = Retrofit.Builder().baseUrl("").addConverterFactory(GsonConverterFactory.create()).build()
    private val ambulanceService = retrofit.create(AmbulanceService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLoginBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.loginPhoneNumberInput.text.toString().trim()
            val password = binding.loginPasswordInput.text.toString().trim()
            val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
            if (!phoneNumber.isMobile() || !password.isPassword()) {

                Snackbar.make(
                    bottomNavView,
                    "PhoneNumber or Password is not valid", Snackbar.LENGTH_LONG,
                ).apply {
                    anchorView = bottomNavView
                    setAction("Dismiss") { }
                }.show()
                return@setOnClickListener
            }
            // Check
            activity?.lifecycleScope?.launch(Dispatchers.IO) {
                try {
                    val response = ambulanceService.check(phoneNumber)
                } catch(e: Exception) {
                   withContext(Dispatchers.Main) {
                       Snackbar.make(
                           bottomNavView,
                           e.message?: "", Snackbar.LENGTH_LONG,
                       ).apply {
                           anchorView = bottomNavView
                           setAction("Dismiss") { }
                       }.show()
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
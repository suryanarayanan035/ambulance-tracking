package com.sample.ambulancetracking.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.FragmentRegisterBinding
import com.sample.common.isMobile
import com.sample.common.isName
import com.sample.common.isPassword

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRegisterBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentRegisterBinding.bind(view)

        binding.registerButton.setOnClickListener {
            val phoneNumber = binding.registerPhoneNumberInput.text.toString().trim()
            val password = binding.registerPasswordInput.text.toString().trim()
            val name = binding.registerNameInput.text.toString().trim()

            if (!phoneNumber.isMobile() || !password.isPassword() || !name.isName()) {
                val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
                Snackbar.make(
                    bottomNavView,
                    "PhoneNumber/Password/Name is not valid", Snackbar.LENGTH_LONG,
                ).apply {
                    anchorView = bottomNavView
                    setAction("Dismiss") { }
                }.show()
                return@setOnClickListener
            }
            // Check
        }
    }

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}
package com.sample.ambulancetracking.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.FragmentRegisterBinding

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

            val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
            Snackbar.make(
                bottomNavView,
                "$phoneNumber $password $name", Snackbar.LENGTH_LONG,
            ).apply {
                anchorView = bottomNavView
                setAction("Dismiss") { }
            }.show()
        }
    }

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}
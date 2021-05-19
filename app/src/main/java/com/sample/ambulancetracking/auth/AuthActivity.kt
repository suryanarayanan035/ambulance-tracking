package com.sample.ambulancetracking.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.sample.ambulancetracking.R
import com.sample.ambulancetracking.databinding.ActivityAuthBinding
import com.sample.ambulancetracking.home.HomeActivity
import com.sample.common.USERID_PREFS

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authPages by lazy {
        arrayOf(
            LoginFragment.newInstance(),
            RegisterFragment.newInstance(),
        )
    }
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(
            getString(R.string.prefs_key),
            Context.MODE_PRIVATE
        )

        binding.authOptionsBottomNav.selectedItemId = R.id.loginMenu
        supportFragmentManager.commit {
            replace(R.id.authPagesContainer, authPages[0])
        }

        binding.authOptionsBottomNav.setOnNavigationItemReselectedListener { menuItem ->
            Log.e("From AuthActivity", "Called ItemReselectedListener for ${menuItem.title}")
        }

        binding.authOptionsBottomNav.setOnNavigationItemSelectedListener { menuItem ->
            return@setOnNavigationItemSelectedListener when(menuItem.itemId) {
                R.id.loginMenu -> {
                    supportFragmentManager.commit {
                        replace(R.id.authPagesContainer, authPages[0])
                    }
                    true
                }
                R.id.registerMenu -> {
                    supportFragmentManager.commit {
                        replace(R.id.authPagesContainer, authPages[1])
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val userId = sharedPref.getString(USERID_PREFS, "")
        if (!userId.isNullOrBlank()) {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
    }
}
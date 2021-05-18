package com.sample.hospitaladmin.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.commit
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val authPages by lazy {
        arrayOf(
            LoginFragment.newInstance(),
            RegisterFragment.newInstance(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
}
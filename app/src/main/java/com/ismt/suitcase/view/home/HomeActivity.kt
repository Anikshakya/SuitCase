package com.ismt.suitcase.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityHomeBinding
import com.ismt.suitcase.view.home.fragment.ExploreFragment
import com.ismt.suitcase.view.home.fragment.ProfileFragment
import com.ismt.suitcase.view.home.fragment.ShopFragment

class HomeActivity : AppCompatActivity() {
    //Initialization
    private val tag = "HomeActivity"
    private lateinit var viewBinding : ActivityHomeBinding
    private val exploreFragment = ExploreFragment.newInstance()
    private val shopFragment = ShopFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Binding
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Set view acc to selected item in bottom nav
        setUpViews()
    }

    private fun setUpViews() {
        setupFragmentContainerView()
        setUpBottomNavigationView()
    }

    private fun setupFragmentContainerView() {
        loadFragmentInFcv(shopFragment)
    }

    private fun setUpBottomNavigationView() {
        viewBinding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {

                R.id.explore -> {
                    loadFragmentInFcv(exploreFragment)
                    true
                }

                R.id.shop -> {
                    loadFragmentInFcv(shopFragment)
                    true
                }

                R.id.profile -> {
                    loadFragmentInFcv(profileFragment)
                    true
                }

                else -> {
                    loadFragmentInFcv(profileFragment)
                    true
                }
            }
        }
    }

    private fun loadFragmentInFcv(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.fcvDashboard.id, fragment)
            .commit()
    }
}
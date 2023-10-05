package com.ismt.suitcase.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityDashboardBinding
import com.ismt.suitcase.view.home.fragment.ExploreFragment
import com.ismt.suitcase.view.home.fragment.ProfileFragment
import com.ismt.suitcase.view.home.fragment.ShopFragment

class DashboardActivity : AppCompatActivity() {
    //Initialization
    private val tag = "DashboardActivity"
    private lateinit var viewBinding : ActivityDashboardBinding
    private val exploreFragment = ExploreFragment.newInstance()
    private val shopFragment = ShopFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //Binding
        viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Set view acc to selected item in bottom nav
        setUpViews()
    }

    private fun setUpViews() {
        setupFragmentContainerView()
        setUpBottomNavigationView()
    }

    private fun setupFragmentContainerView() {
        loadFragmentInFcv(shopFragment) // selects the initial page to show in the bottom nav
        viewBinding.bottomNavigationView.selectedItemId = R.id.shop // Set the initial selected index of the bottom nav
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
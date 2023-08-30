package com.ismt.suitcase.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityHomeBinding
import com.ismt.suitcase.utils.SharedPrefUtils
import com.ismt.suitcase.view.LoginActivity

class HomeActivity : AppCompatActivity() {
    //Initialization
    private val tag = "HomeActivity"
    private lateinit var viewBinding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Binding
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        var sharedPref = SharedPrefUtils(this)

        //Logout Button Behaviour
        viewBinding.btnLogout.setOnClickListener {
            //Clear saved user login state
            sharedPref.removeKey(AppConstants.KEY_IS_LOGGED_IN)
            sharedPref.removeKey(AppConstants.KEY_EMAIL)

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }

        //Set the Text view value from stored email data
        viewBinding.tvEmail.text = sharedPref.getString(AppConstants.KEY_EMAIL)
    }
}
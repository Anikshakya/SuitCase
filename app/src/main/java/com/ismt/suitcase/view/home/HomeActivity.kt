package com.ismt.suitcase.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Binding
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        var sharedPref = SharedPrefUtils(this)
        //For Google Sign Out
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))// This token is important for signing in/out to google
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)


        //Logout Button Behaviour
        viewBinding.btnLogout.setOnClickListener {
            //Clear saved user login state
            sharedPref.removeKey(AppConstants.KEY_IS_LOGGED_IN)
            sharedPref.removeKey(AppConstants.KEY_EMAIL)

            gsc.signOut() //Google Sign Out
            FirebaseAuth.getInstance().signOut() //Firebase Sign Out
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }

        //Set the Text view value from stored email data
        viewBinding.tvEmail.text = sharedPref.getString(AppConstants.KEY_EMAIL)
    }
}
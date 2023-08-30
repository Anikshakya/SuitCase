package com.ismt.suitcase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityLoginBinding
import com.ismt.suitcase.utils.SharedPrefUtils
import com.ismt.suitcase.view.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    //Check if the user is signed in or not
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //Navigate to Home Page
            val intent = Intent(this, HomeActivity :: class.java)
            intent.putExtra(AppConstants.KEY_EMAIL, currentUser.email)//Send Data to next page
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Binding
        auth = FirebaseAuth.getInstance()
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //On Click Of Login Button
        viewBinding.btnLoginButton.setOnClickListener {
            val email = viewBinding.etEmail.text.toString().trim()
            val password = viewBinding.etPassword.text.toString().trim()

            if(email.isEmpty()){
                ToastUtils.showToast(this, "Enter a valid Email")
                return@setOnClickListener
            }

            if(password.isEmpty()){
                ToastUtils.showToast(this, "Password cannot be empty")
                return@setOnClickListener
            }

            //Login Using Firebase
            loginWithFirebase(email, password)
        }

        //Google Sign In Button On Click
        viewBinding.btnGoogleSignIn.setOnClickListener{
            //TODO Google Sign In
        }

        //Register Here Onclick Behaviour
        viewBinding.tvRegisterHere.setOnClickListener {
            val intent = Intent(this, RegisterActivity :: class.java)
            startActivity(intent)
        }

        //Forgot Password Onclick Behaviour
        viewBinding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity :: class.java)
            startActivity(intent)
        }

    }

    //Login Using Firebase
    private fun loginWithFirebase(email: String, password: String){
        startLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //StopLoading Indicator
                stopLoading()
                //Store User Data in Shared Pref
                var sharedPref = SharedPrefUtils(this)
                sharedPref.saveBoolean(AppConstants.KEY_IS_LOGGED_IN, true)
                sharedPref.saveString(AppConstants.KEY_EMAIL, email)

                ToastUtils.showToast(this, "Logged in Successfully")

                //Navigate to Home Page
                val intent = Intent(this, HomeActivity :: class.java)
                intent.putExtra(AppConstants.KEY_EMAIL, email)//Send Data to next page
                startActivity(intent)
                finish()
            } else {
                stopLoading()
                ToastUtils.showToast(this, task.exception?.message.toString())
            }
        }
    }

    //Start Loading
    private fun startLoading(){
        //Display Loading
        viewBinding.pbLoginLoading.visibility = View.VISIBLE
        //Hide Register Button on loading
        viewBinding.btnLoginButton.visibility = View.GONE
    }

    //Stop Loading
    private fun stopLoading(){
        //Hide Loading
        viewBinding.pbLoginLoading.visibility = View.GONE
        //Display Register Button on loading
        viewBinding.btnLoginButton.visibility = View.VISIBLE
    }
}
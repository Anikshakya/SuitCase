package com.ismt.suitcase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityLoginBinding
import com.ismt.suitcase.utils.SharedPrefUtils

class LoginActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Binding
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        //Login Button Onclick Behaviour
        viewBinding.btnLoginButton.setOnClickListener {
            val email = viewBinding.etEmail.text.toString().trim()
            val password = viewBinding.etPassword.text.toString().trim()

            //TextField validations
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                viewBinding.emailInputLayout.error = "Enter a valid Email"
                ToastUtils.showToast(this, "Enter a valid Email")
            } else if(password.isBlank()){
                viewBinding.passwordInputLayout.error = "Password cannot be empty"
                ToastUtils.showToast(this, "Password cannot be empty")
            } else{
                if(password == "tester"){
                    //TODO Login user, auth

                    //Writing into shared preference
                    var sharedPref = SharedPrefUtils(this)
                    sharedPref.saveBoolean(AppConstants.KEY_IS_LOGGED_IN, true)
                    sharedPref.saveString(AppConstants.KEY_EMAIL, email)


                    val intent = Intent(this, HomeActivity :: class.java)
                    intent.putExtra(AppConstants.KEY_EMAIL, email)//Send Data to next page
                    startActivity(intent)
                    finish()
                } else{
                    ToastUtils.showToast(this, "Invalid User")
                }
            }
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
}
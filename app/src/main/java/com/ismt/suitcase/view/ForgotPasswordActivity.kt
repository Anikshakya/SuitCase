package com.ismt.suitcase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.ismt.suitcase.R

class ForgotPasswordActivity : AppCompatActivity() {//Initialization
    private val tag = "ForgotPasswordActivity"
    private lateinit var btnForgotPassword : Button
    private lateinit var etEmail : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //Bindings
        btnForgotPassword = findViewById(R.id.btn_resetPasswordButton)
        etEmail = findViewById(R.id.et_email)

        //Forgot Password Onclick Behaviour
        btnForgotPassword.setOnClickListener {
            //TODO Forgot password Api
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
    }
}
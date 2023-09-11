package com.ismt.suitcase.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.ismt.suitcase.R


class ForgotPasswordActivity : AppCompatActivity() {//Initialization
    private val tag = "ForgotPasswordActivity"
    private lateinit var btnForgotPassword : Button
    private lateinit var etEmail : EditText
    private lateinit var pbForgotPassword : ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //Bindings
        auth = FirebaseAuth.getInstance() //Firebase Auth
        btnForgotPassword = findViewById(R.id.btn_resetPasswordButton)
        etEmail = findViewById(R.id.et_email)
        pbForgotPassword = findViewById(R.id.pb_forgotPasswordLoading)

        //Forgot Password Onclick Behaviour
        btnForgotPassword.setOnClickListener {
            //Start Loading
            startLoading();
            val email = etEmail.text.toString().trim()
            if(email.isEmpty()){
                ToastUtils.showToast(this, "Enter a valid Email")
                return@setOnClickListener
            }

            // Send Reset Email
            auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    stopLoading();
                    // Password reset email sent successfully
                    Toast.makeText(
                        applicationContext,
                        "Password reset email sent",
                        Toast.LENGTH_SHORT
                    ).show()

                    //Navigate to Login on Success
                    val intent = Intent(this, LoginActivity :: class.java)
                    startActivity(intent)
                    finish()
                } else {
                    stopLoading();
                    // Password reset email failed to send
                    Toast.makeText(
                        applicationContext,
                        "Password reset email failed to send",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    //Start Loading
    private fun startLoading(){
        //Display Loading
        pbForgotPassword.visibility = View.VISIBLE
        //Hide ForgotPassword Button on loading
        btnForgotPassword.visibility = View.GONE
    }

    //Stop Loading
    private fun stopLoading(){
        //Hide Loading
        pbForgotPassword.visibility = View.GONE
        //Display ForgotPassword Button on loading
        btnForgotPassword.visibility = View.VISIBLE
    }
}
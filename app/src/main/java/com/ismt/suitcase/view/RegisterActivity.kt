package com.ismt.suitcase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Binding
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Register Button Onclick Behaviour
        viewBinding.btnRegisterButton.setOnClickListener {
            //TODO register user
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }

        //Login Here Onclick Behaviour
        viewBinding.tvLoginHere.setOnClickListener {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }

        //Back button onclick
        viewBinding.ibBack.setOnClickListener {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }
    }

    //Clear input fields data
    private fun clearInputFields(){
        viewBinding.etEmail.text?.clear()
        viewBinding.etEmail.text?.clear()
        viewBinding.etPassword.text?.clear()
        viewBinding.etConfirmPassword.text?.clear()
    }
}
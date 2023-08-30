package com.ismt.suitcase.view

import ToastUtils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Binding
        auth = FirebaseAuth.getInstance()
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Register Button Onclick Behaviour
        viewBinding.btnRegisterButton.setOnClickListener {
            val name = viewBinding.etName.text.toString().trim()
            val email = viewBinding.etEmail.text.toString().trim()
            val password = viewBinding.etPassword.text.toString().trim()
            val confirmPassword = viewBinding.etConfirmPassword.text.toString().trim()

            if(name.isEmpty()){
                ToastUtils.showToast(this, "Please enter your Name")
                return@setOnClickListener
            }

            if(email.isEmpty()){
                ToastUtils.showToast(this, "Email required")
                return@setOnClickListener
            }

            if(password.isEmpty()){
                ToastUtils.showToast(this, "Password required")
                return@setOnClickListener
            }

            if(password != confirmPassword){
                ToastUtils.showToast(this, "The Password does not match")
            }
            else{
                //Register Using Firebase
                firebaseRegister(email, password)
            }
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

    //Register User in Firebase
    private fun firebaseRegister(email : String, password : String){
        startLoading()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                stopLoading()
                ToastUtils.showToast(this, "User Registered")
                //Redirect to login screen
                val intent = Intent(this, LoginActivity::class.java)
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
        viewBinding.pbRegisterLoading.visibility = View.VISIBLE
        //Hide Register Button on loading
        viewBinding.btnRegisterButton.visibility = View.GONE
    }

    //Stop Loading
    private fun stopLoading(){
        //Hide Loading
        viewBinding.pbRegisterLoading.visibility = View.GONE
        //Display Register Button on loading
        viewBinding.btnRegisterButton.visibility = View.VISIBLE
    }
}
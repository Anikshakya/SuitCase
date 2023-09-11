package com.ismt.suitcase.view

import ToastUtils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Binding
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
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
                firebaseRegister(name, email, password)

//                //Run Room database in new thread to stop screen freeze and rec by room
//                Thread{
//                    try {
//                        //Data from register form
//                        val user = User(
//                            name,
//                            email,
//                            password,
//                        )
//                        //Insert into Room Database
//                        val testDatabase = UserDatabase.getInstance(applicationContext) //Instantiate test database
//                        val userDao = testDatabase.userDao() //Instantiate userDao
//                        val userExistInDb = userDao.checkUserExist(email)
//                        if(userExistInDb == null) {
//                            userDao.insertUser(user)
//                            runOnUiThread {
//                                ToastUtils.showToast(this, "User Registered")
//                            }
//                            //Clear input fields
//                            clearInputFields()
//                            //Redirect
//                            val intent = Intent(this, LoginActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            runOnUiThread {
//                                ToastUtils.showToast(this, "User already exists")
//                            }
//                        }
//                    } catch (e: Exception){
//                        runOnUiThread {
//                            ToastUtils.showToast(this, "Couldn't register user!")
//                        }
//                        e.printStackTrace()
//                    }
//                }.start()
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
        viewBinding.etName.text?.clear()
        viewBinding.etEmail.text?.clear()
        viewBinding.etPassword.text?.clear()
        viewBinding.etConfirmPassword.text?.clear()
    }

    //Register User in Firebase
    private fun firebaseRegister(name: String, email : String, password : String){
        startLoading()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //Store User to Firestore
                storeUserToFirestore(name, email)
                stopLoading()
                ToastUtils.showToast(this, "User Registered")
                auth.signOut()
                clearInputFields()
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

    //Store user in Firestore
    private fun storeUserToFirestore(name: String, email : String){
        //Store in Firestore
        val userData = HashMap<String,Any>()
        userData["firstName"] = name
        userData["email"] = email
        userData["profileUrl"] = ""

        db.collection("users").document(email).set(userData)
            .addOnSuccessListener {
            //Redirect to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        .addOnFailureListener {
            ToastUtils.showToast(this,"Unable To add user Data")
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
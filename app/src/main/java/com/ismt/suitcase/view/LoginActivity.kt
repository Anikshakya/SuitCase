package com.ismt.suitcase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityLoginBinding
import com.ismt.suitcase.utils.SharedPrefUtils
import com.ismt.suitcase.view.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    //Initialization
    private lateinit var viewBinding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var gso: GoogleSignInOptions //For Google Sign In
    private lateinit var gsc: GoogleSignInClient //For Google Sign In

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
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = FirebaseAuth.getInstance() //Firebase Auth
        db = FirebaseFirestore.getInstance() //Firebase Firestore
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))// This token is important for signing in to google
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

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
            val intent: Intent = gsc.signInIntent
            startActivityForResult(intent, 100)
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

    //Detects the result from implicit intent to this page
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            loginInWithGoogle(data)
        } else{

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

    //Login Using Google
    private fun loginInWithGoogle(data: Intent?) {
        val result = GoogleSignIn.getSignedInAccountFromIntent(data)

        if (result != null) {
            try {
                val account = result.getResult(ApiException::class.java)

                // Check if user actually selected an account
                if (account != null && account.idToken != null) {
                    // Start Google Login Loading
                    startGoogleLoading()

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                //Save in Shared Pref
                                var sharedPref = SharedPrefUtils(this)
                                sharedPref.saveBoolean(AppConstants.KEY_IS_LOGGED_IN, true)
                                sharedPref.saveString(AppConstants.KEY_EMAIL, account.email.toString())

                                //Store User to Firestore
                                val userData = HashMap<String,Any>()
                                userData["firstName"] = account.givenName.toString()
                                userData["email"] = account.email.toString()
                                userData["profileUrl"] = account.photoUrl.toString()
                                db.collection("users").document(account.email.toString()).set(userData)
                                    .addOnSuccessListener {
                                        ToastUtils.showToast(this,"Signed in Successfully")
                                    }
                                    .addOnFailureListener {
                                        ToastUtils.showToast(this,"Unable To add user data")
                                    }

                                //Stop Google Login Loading
                                stopGoogleLoading()

                                //Navigate to next page
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.putExtra(AppConstants.KEY_EMAIL, account.email.toString())//Send Data to next page
                                startActivity(intent)
                                finish()
                            } else {
                                // Stop Google Login Loading
                                stopGoogleLoading()
                                Toast.makeText(this, "Couldn't Sign In", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    // Handle case where user didn't select an account
                    stopGoogleLoading()
                    Toast.makeText(this, "Google Sign-In Failed: No account selected", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                // Handle the exception
                stopGoogleLoading()
                Toast.makeText(this, "Google Sign-In Failed: " + e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            // Handle case where result is null
            stopGoogleLoading()
            Toast.makeText(this, "Google Sign-In Failed: No result", Toast.LENGTH_LONG).show()
        }
    }

    //Start Loading
    private fun startLoading(){
        //Display Loading
        viewBinding.pbLoginLoading.visibility = View.VISIBLE
        //Hide Login Button on loading
        viewBinding.btnLoginButton.visibility = View.GONE
    }

    //Stop Loading
    private fun stopLoading(){
        //Hide Loading
        viewBinding.pbLoginLoading.visibility = View.GONE
        //Display Login Button on loading
        viewBinding.btnLoginButton.visibility = View.VISIBLE
    }

    //Start Google Loading
    private fun startGoogleLoading(){
        //Display Loading
        viewBinding.pbGoogleLoginLoading.visibility = View.VISIBLE
        //Hide Register Button on loading
        viewBinding.btnGoogleSignIn.visibility = View.GONE
    }

    //Stop Google Loading
    private fun stopGoogleLoading(){
        //Hide Loading
        viewBinding.pbGoogleLoginLoading.visibility = View.GONE
        //Display Register Button on loading
        viewBinding.btnGoogleSignIn.visibility = View.VISIBLE
    }
}
package com.ismt.suitcase.view.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.FragmentProfileBinding
import com.ismt.suitcase.utils.SharedPrefUtils
import com.ismt.suitcase.view.LoginActivity


class ProfileFragment : Fragment() {
    private lateinit var profileBinding : FragmentProfileBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        //----------Bindings----------
        var sharedPref = SharedPrefUtils(requireActivity())
        //For Google Sign Out
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))// This token is important for signing in/out to google
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireActivity(), gso)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        if (account != null) {
            if (account.displayName != null && account.displayName.toString() != "") {
                profileBinding.userName.text = account.displayName.toString()
            } else {
                profileBinding.userName.text = "John Doe"
            }
            val email = account.email?.toString()
            val photoUrl = account.photoUrl

            profileBinding.tvEmail.text = email
            if (photoUrl != null) {
                Glide.with(requireActivity()).load(photoUrl).into(profileBinding.ivItemImage)
            }
        }

        //Logout Button Behaviour
        profileBinding.btnLogout.setOnClickListener {
            //Clear saved user login state
            sharedPref.removeKey(AppConstants.KEY_IS_LOGGED_IN)
            sharedPref.removeKey(AppConstants.KEY_EMAIL)

            gsc.signOut() //Google Sign Out
            FirebaseAuth.getInstance().signOut() //Firebase Sign Out
            val intent = Intent(requireActivity(), LoginActivity :: class.java)
            startActivity(intent)
        }

        //Set the Text view value from stored email data
        profileBinding.tvEmail.text = sharedPref.getString(AppConstants.KEY_EMAIL)

        // Inflate the layout for this fragment
        return profileBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
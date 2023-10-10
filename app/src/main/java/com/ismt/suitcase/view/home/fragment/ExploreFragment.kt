package com.ismt.suitcase.view.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.FragmentExploreBinding
import com.ismt.suitcase.databinding.FragmentProfileBinding

class ExploreFragment : Fragment() {
    private lateinit var exploreFragment: FragmentExploreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        exploreFragment = FragmentExploreBinding.inflate(layoutInflater, container, false)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if(account!!.displayName != null && account!!.displayName.toString() != ""){
            exploreFragment.tvGreetings.text = "Hi " + account!!.displayName.toString().split(" ")[0]
        } else {
            exploreFragment.tvGreetings.text = "Welcome"
        }

        // Inflate the layout for this fragment
        return exploreFragment.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}
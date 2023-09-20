package com.ismt.suitcase.view.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismt.suitcase.databinding.FragmentShopBinding
import com.ismt.suitcase.view.home.AddOrUpdateActivity

class ShopFragment : Fragment() {
    private lateinit var shopBinding: FragmentShopBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shopBinding = FragmentShopBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        setUpViews()
        return shopBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopFragment()
    }

    private fun setUpViews() {
        setUpFloatingActionButton()
    }

    private fun setUpFloatingActionButton() {
        shopBinding.fabAddItem.setOnClickListener {
            val intent = Intent(requireActivity(), AddOrUpdateActivity::class.java)
            startActivity(intent)
//            startAddOrUpdateActivityForResult.launch(intent)
        }
    }
}
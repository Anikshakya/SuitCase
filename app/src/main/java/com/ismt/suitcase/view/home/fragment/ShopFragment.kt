package com.ismt.suitcase.view.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.FragmentShopBinding
import com.ismt.suitcase.room.Product
import com.ismt.suitcase.room.SuitcaseDatabase
import com.ismt.suitcase.view.home.AddOrUpdateActivity
import com.ismt.suitcase.view.home.ProductDetailActivity
import com.ismt.suitcase.view.home.adapters.ProductRecyclerAdapter

class ShopFragment : Fragment(), ProductRecyclerAdapter.ProductAdapterListener {
    private lateinit var shopBinding: FragmentShopBinding
    private lateinit var productRecyclerAdapter: ProductRecyclerAdapter

    private val startAddOrUpdateActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
            setUpRecyclerView()
        } else {
            //TODO Do nothing
        }
    }

    private val startDetailViewActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == ProductDetailActivity.RESULT_CODE_REFRESH) {
            setUpRecyclerView()
        } else {
            //Do Nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == ProductDetailActivity.RESULT_CODE_REFRESH) {
                setUpRecyclerView()
            } else {
                //Do Nothing
            }
        }

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
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        //TODO fetch data from source (remote server)
        val suitCaseDatabase = SuitcaseDatabase.getInstance(requireActivity().applicationContext)
        val productDao = suitCaseDatabase.productDao()

        Thread {
            try {
                val products = productDao.getAllProducts()
                if (products.isEmpty()) {
                    requireActivity().runOnUiThread {
                        ToastUtils.showToast(requireActivity(), "No Items Yet!")
                    }
                }
                requireActivity().runOnUiThread {
                    populateRecyclerView(products)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                requireActivity().runOnUiThread {
                    ToastUtils.showToast(requireActivity(), "Couldn't load items.")
                }
            }
        }.start()
    }

    private fun populateRecyclerView(products: List<Product>) {
        productRecyclerAdapter = ProductRecyclerAdapter(
            products,
            this,
            requireActivity().applicationContext
        )
        shopBinding.rvShop.adapter = productRecyclerAdapter
        shopBinding.rvShop.layoutManager = LinearLayoutManager(requireActivity())

        // TODO add grid layout
//        shopBinding.rvShop.layoutManager = GridLayoutManager(activity, 2)
//        (shopBinding.rvShop.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return when (adapter.getItemViewType(position)) {
//                    MenuAdapter.ITEM -> 1
//                    MenuAdapter.FULLSIZE -> 2
//                    else -> 1
//                }
//            }
//        }
    }


    private fun setUpFloatingActionButton() {
        shopBinding.fabAddItem.setOnClickListener {
            val intent = Intent(requireActivity(), AddOrUpdateActivity::class.java)
            startAddOrUpdateActivityForResult.launch(intent)
        }
    }

    override fun onItemClicked(product: Product, position: Int) {
        val intent = Intent(requireActivity(), ProductDetailActivity::class.java)
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        intent.putExtra(AppConstants.KEY_PRODUCT_POSITION, position)
        startDetailViewActivity.launch(intent)
    }
}
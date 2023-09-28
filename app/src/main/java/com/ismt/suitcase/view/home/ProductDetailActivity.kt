package com.ismt.suitcase.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityProductDetailBinding
import com.ismt.suitcase.room.Product

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var detailViewBinding: ActivityProductDetailBinding
    private var product: Product? = null
    private var position: Int = -1

    companion object {
        const val RESULT_CODE_REFRESH = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewBinding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(detailViewBinding.root)

        //Receiving Intent Data
        product = intent.getParcelableExtra(AppConstants.KEY_PRODUCT)
        position = intent.getIntExtra(AppConstants.KEY_PRODUCT_POSITION, -1)
    }
}
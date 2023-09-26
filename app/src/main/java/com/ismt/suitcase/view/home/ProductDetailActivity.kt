package com.ismt.suitcase.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ismt.suitcase.R

class ProductDetailActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CODE_REFRESH = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
    }
}
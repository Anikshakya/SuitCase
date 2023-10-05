package com.ismt.suitcase.view.home

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityProductDetailBinding
import com.ismt.suitcase.room.Product
import com.ismt.suitcase.room.SuitcaseDatabase
import com.ismt.suitcase.utils.BitmapScalar
import java.io.IOException

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var detailViewBinding: ActivityProductDetailBinding
    private var product: Product? = null
    private var position: Int = -1

    private val startAddItemActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
            val product = it.data?.getParcelableExtra<Product>(AppConstants.KEY_PRODUCT)
            populateDataToTheViews(product)
        }
    }

    companion object {
        const val RESULT_CODE_REFRESH = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewBinding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(detailViewBinding.root)

        // Receiving Intent Data
        product = intent.getParcelableExtra(AppConstants.KEY_PRODUCT)
        position = intent.getIntExtra(AppConstants.KEY_PRODUCT_POSITION, -1)

        // Populate product data in the view
        populateDataToTheViews(product)
        // Set the buttons functionality in product details page
        setUpButtons()
    }

    // Populate
    private fun populateDataToTheViews(product: Product?) {
        product?.apply {
            detailViewBinding.productTitle.text = this.title
            detailViewBinding.productPrice.text = this.price
            detailViewBinding.productDescription.text = this.description
            detailViewBinding.productImage.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        Uri.parse(product.image)
                    )
                    bitmap = BitmapScalar.stretchToFill(
                        bitmap,
                        detailViewBinding.productImage.width,
                        detailViewBinding.productImage.height
                    )
                    detailViewBinding.productImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            // TODO populate location
        }
    }

    // Set Up Buttons
    private fun setUpButtons() {
        setUpBackButton()
        setUpEditButton()
        setUpDeleteButton()
    }

    // Back Button Behavior
    private fun setUpBackButton() {
        detailViewBinding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_REFRESH)
        }
    }

    private fun setUpEditButton() {
        detailViewBinding.ibEdit.setOnClickListener {
            val intent = Intent(this, AddOrUpdateActivity::class.java).apply {
                this.putExtra(AppConstants.KEY_PRODUCT, product)
            }
            startAddItemActivity.launch(intent)
        }
    }

    private fun setUpDeleteButton() {
        detailViewBinding.ibDelete.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, _ -> deleteProduct() }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
        }
    }

    // Delete Product
    private fun deleteProduct() {
        val testDatabase = SuitcaseDatabase.getInstance(this.applicationContext)
        val productDao = testDatabase.productDao()

        Thread {
            try {
                product?.apply {
                    productDao.deleteProduct(this)
                    runOnUiThread {
                        ToastUtils.showToast(
                            this@ProductDetailActivity,
                            "Product deleted successfully"
                        )
                        setResultWithFinish(RESULT_CODE_REFRESH)
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                runOnUiThread {
                    ToastUtils.showToast(
                        this@ProductDetailActivity,
                        "Cannot delete product."
                    )
                }
            }
        }.start()
    }

    // Set the result with finish
    private fun setResultWithFinish(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResultWithFinish(RESULT_CODE_REFRESH)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

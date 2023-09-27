package com.ismt.suitcase.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityAddOrUpdateBinding
import com.ismt.suitcase.room.Product
import com.ismt.suitcase.room.SuitcaseDatabase

class AddOrUpdateActivity : AppCompatActivity() {
    private lateinit var addOrUpdateBinding : ActivityAddOrUpdateBinding

    companion object{
        const val RESULT_CODE_COMPLETE = 1001
        const val RESULT_CODE_CANCEL = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Bindings
        addOrUpdateBinding = ActivityAddOrUpdateBinding.inflate(layoutInflater)
        setContentView(addOrUpdateBinding.root)

        // On Press of Back Button
        addOrUpdateBinding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_CANCEL)
        }

        // On Submit Button Behaviour
        addOrUpdateBinding.mbSubmit.setOnClickListener {
            var title = addOrUpdateBinding.tieTitle.text.toString().trim()
            var price = addOrUpdateBinding.tiePrice.text.toString().trim()
            var desc = addOrUpdateBinding.tieDescription.text.toString().trim()

            // Validation: Check if fields are not empty
            if (title.isEmpty()) {
                // Display an error message for the title field
                addOrUpdateBinding.tieTitle.error = "Title cannot be empty"
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                // Display an error message for the price field
                addOrUpdateBinding.tiePrice.error = "Price cannot be empty"
                return@setOnClickListener
            }

            if (desc.isEmpty()) {
                // Display an error message for the description field
                addOrUpdateBinding.tieDescription.error = "Description cannot be empty"
                return@setOnClickListener
            }

            // Start Submit Button Loading
            startLoading()

            // Set The Data In Room Database
            val product = Product(
                title,
                price,
                desc,
                "",
                ""
            )

            //Instance of Existing Database
            val suitcaseDatabase = SuitcaseDatabase.getInstance(applicationContext)
            val productDao = suitcaseDatabase.productDao()

            Thread{
                try {
                    // Insert into Product Database
                    productDao.insertNewProduct(product)
                    runOnUiThread {
                        //Clear the Input Fields
                        clearInputFields()

                        //Stop Submit Button Loading
                        stopLoading()

                        setResultWithFinish(RESULT_CODE_COMPLETE)

                        ToastUtils.showToast(this, "Product Added!")
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    //Stop Submit Button Loading
                    stopLoading()

                    runOnUiThread {
                        ToastUtils.showToast(this, "Couldn't Add Product!")
                    }
                }
            }.start()

        }
    }

    // Send Result code on exiting the page
    private fun setResultWithFinish(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    //Clear input fields data
    private fun clearInputFields(){
        addOrUpdateBinding.tieTitle.text?.clear()
        addOrUpdateBinding.tiePrice.text?.clear()
        addOrUpdateBinding.tieDescription.text?.clear()
    }

    //Start Loading
    private fun startLoading(){
        //Display Loading
        addOrUpdateBinding.pbSubmitLoading.visibility = View.VISIBLE
        //Hide Login Button on loading
        addOrUpdateBinding.mbSubmit.visibility = View.GONE
    }

    //Stop Loading
    private fun stopLoading(){
        //Hide Loading
        addOrUpdateBinding.pbSubmitLoading.visibility = View.GONE
        //Display Login Button on loading
        addOrUpdateBinding.mbSubmit.visibility = View.VISIBLE
    }
}
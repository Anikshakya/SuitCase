package com.ismt.suitcase.view.home

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.telephony.SmsManager
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityProductDetailBinding
import com.ismt.suitcase.room.Product
import com.ismt.suitcase.room.SuitcaseDatabase
import com.ismt.suitcase.utils.GeoCoding
import java.io.IOException

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var detailViewBinding: ActivityProductDetailBinding
    private var product: Product? = null
    private var position: Int = -1
    private var pickedContact: TextInputEditText? = null
    private lateinit var startAddItemActivity: ActivityResultLauncher<Intent>
    private lateinit var startContactActivityForResult: ActivityResultLauncher<Intent>

    companion object {
        const val RESULT_CODE_REFRESH = 2001
        const val SMS_PERMISSIONS_REQUEST_CODE = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewBinding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(detailViewBinding.root)

        // Register the ActivityResultLaunchers
        bindAddOrUpdateActivityForResult()
        bindContactPickerActivityForResult()

        // Receiving Intent Data
        product = intent.getParcelableExtra(AppConstants.KEY_PRODUCT)
        position = intent.getIntExtra(AppConstants.KEY_PRODUCT_POSITION, -1)

        // Populate product data in the view
        populateDataToTheViews(product)
        // Set the buttons functionality in product details page
        setUpButtons()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSIONS_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                showSmsBottomSheetDialog()
            } else {
                ToastUtils.showToast(this, "SMS permission not granted")
            }
        }
    }


    // Populate
    private fun populateDataToTheViews(product: Product?) {
        product?.apply {
            detailViewBinding.productTitle.text = this.title
            detailViewBinding.productPrice.text = "$"+this.price
            detailViewBinding.productDescription.text = this.description
            detailViewBinding.cbPurchased.isChecked = (product?.isPurchased == true)

            // For location
            var productLocation = this.location;
            if(productLocation != ""){
                val lat = productLocation!!.split(",")[0]
                val lng = productLocation!!.split(",")[1]
                val geoCodedAddress = GeoCoding.reverseTheGeoCodeToAddress(applicationContext, lat, lng)
                detailViewBinding.productLocation.text = geoCodedAddress
            } else {
                detailViewBinding.productLocation.text = "No Location Found"
            }

            detailViewBinding.productImage.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        Uri.parse(product.image)
                    )
//                    bitmap = BitmapScalar.stretchToFill(
//                        bitmap,
//                        detailViewBinding.productImage.width,
//                        detailViewBinding.productImage.height
//                    )
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
        setUpShareButton()
        setUpMarkAsPurchasedCheckbox()
    }

    // Back Button Behavior
    private fun setUpBackButton() {
        detailViewBinding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_REFRESH)
        }
    }

    // Set Up Edit Button
    private fun setUpEditButton() {
        detailViewBinding.ibEdit.setOnClickListener {
            val intent = Intent(this, AddOrUpdateActivity::class.java).apply {
                this.putExtra(AppConstants.KEY_PRODUCT, product)
            }
            startAddItemActivity.launch(intent)
        }
    }

    // Set Up Delete Button
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

    // Set up the sms button
    private fun setUpShareButton() {
        detailViewBinding.ibShare.setOnClickListener {
            if (areSmsPermissionsGranted(this)) {
                showSmsBottomSheetDialog()
            } else {
                requestPermissions(
                    smsPermissionsList()!!.toTypedArray(),
                    SMS_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    // Check for sms permission
    private fun areSmsPermissionsGranted(context: Context): Boolean {
        val sendSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
        val readSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
        return sendSmsPermission == PackageManager.PERMISSION_GRANTED && readSmsPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun smsPermissionsList(): List<String>? {
        val smsPermissions: MutableList<String> = ArrayList()
        smsPermissions.add(Manifest.permission.READ_SMS)
        smsPermissions.add(Manifest.permission.SEND_SMS)
        return smsPermissions
    }

    // Display sms bottom sheet
    private fun showSmsBottomSheetDialog() {
        val smsBottomSheetDialog = BottomSheetDialog(this)
        smsBottomSheetDialog.setContentView(R.layout.bottom_sheet_send_sms)

        val tilContact: TextInputLayout? = smsBottomSheetDialog.findViewById(R.id.til_contact)
        pickedContact = smsBottomSheetDialog.findViewById(R.id.tie_contact)
        val btnContact: Button? = smsBottomSheetDialog.findViewById(R.id.btn_contact)
        val sendSmsButton: MaterialButton? = smsBottomSheetDialog.findViewById(R.id.mb_send_sms)

        // Set the background color for the button
        sendSmsButton?.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        // Set the text color for the button
        sendSmsButton?.setTextColor(Color.WHITE)
        // Set the background color for the button
        btnContact?.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        // Set the text color for the button
        btnContact?.setTextColor(Color.WHITE)

        btnContact?.setOnClickListener {
            val pickContact = Intent(Intent.ACTION_PICK)
            pickContact.setDataAndType(
                ContactsContract.Contacts.CONTENT_URI,
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
            startContactActivityForResult.launch(pickContact)
        }

        sendSmsButton?.setOnClickListener {
            val contact = pickedContact?.text.toString()
            //TODO validation
            if (contact.isBlank()) {
                tilContact?.error = "Enter Contact"
            } else {
                sendSms(contact)
                smsBottomSheetDialog.dismiss()
            }
            sendSms(contact)
        }

        smsBottomSheetDialog.show()
    }

    private fun sendSms(contact: String) {
        val smsManager: SmsManager = SmsManager.getDefault()
        val description = if (product!!.description.length > 50) {
            product!!.description.substring(0, 50)
        } else {
            product!!.description
        }

        val message = """
        Item: ${product!!.title}
        Price: "$"+${product!!.price}
        Description: $description
        """.trimIndent()
//        smsManager.sendTextMessage(
//            contact,
//            null,
//            message,
//            null,
//            null
//        )

        // If the above SMS manager didn't work
        openSmsAppToSendMessage(contact, message)
    }

    // Open  message app
    private fun openSmsAppToSendMessage(contact: String, message: String) {
        val smsUri = Uri.parse("smsto:$contact")
        val intent = Intent(Intent.ACTION_SENDTO, smsUri)
        intent.putExtra("sms_body", message)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no SMS app is available
            Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the contact
    private fun fetchContactNumberFromData(data: Intent) {
        val contactUri = data.data

        // Specify which fields you want
        // your query to return values for
        val queryFields = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        this.contentResolver
            .query(
                contactUri!!,
                null,
                null,
                null,
                null
            ).use { cursor ->
                // Double-check that you
                // actually got results
                if (cursor!!.count == 0) return

                // Pull out the first column of
                // the first row of data
                // that is your contact's name
                cursor.moveToFirst()
                val contactNumberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactNumber = cursor.getString(contactNumberIndex).apply {
                    //Replacing the brackets and hyphens with empty string as we don't need here
                    this.replace(
                        Regex("[()\\-\\s]+"),
                        ""
                    )
                }
                pickedContact?.setText(contactNumber)
            }
    }


    // Bind AddUpdate page
    private fun bindAddOrUpdateActivityForResult() {
        startAddItemActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
                val product = it.data?.getParcelableExtra<Product>(AppConstants.KEY_PRODUCT)
                populateDataToTheViews(product)
            } else {
                // TODO do nothing
            }
        }
    }

    private fun bindContactPickerActivityForResult() {
        startContactActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    fetchContactNumberFromData(data)
                } else {
                    // Handle the case where data is null (e.g., user canceled)
                    // You can display a message to the user or take appropriate action here.
                }
            } else {
                // Handle the case where the contact selection activity returned a non-OK result
                // You can display a message to the user or take appropriate action here.
            }
        }
    }


    // Mark as purchased
    private fun setUpMarkAsPurchasedCheckbox() {
        detailViewBinding.cbPurchased.setOnCheckedChangeListener { _, isChecked ->
            handleCheckChangedForMarkAsPurchased(
                isChecked
            )
        }
    }

    private fun handleCheckChangedForMarkAsPurchased(isChecked: Boolean) {
        if (isChecked) {
            updateProductWithMarkAsPurchasedTrue()
        } else {
            updateProductWithMarkAsPurchasedFalse()
        }
    }

    private fun updateProductWithMarkAsPurchasedTrue() {
        product?.isPurchased = true
        updateProductDataInDb(product)
    }

    private fun updateProductWithMarkAsPurchasedFalse() {
        product?.isPurchased = false
        updateProductDataInDb(product)
    }

    private fun updateProductDataInDb(product: Product?) {
        val testDatabase = SuitcaseDatabase.getInstance(this.applicationContext)
        val productDao = testDatabase.productDao()

        Thread {
            try {
                product?.apply {
                    productDao.updateProduct(this)
                    runOnUiThread {
                        ToastUtils.showToast(
                            this@ProductDetailActivity,
                            "Product updated successfully"
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                runOnUiThread {
                    ToastUtils.showToast(
                        this@ProductDetailActivity,
                        "Cannot update product."
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
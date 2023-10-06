package com.ismt.suitcase.view.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ismt.suitcase.R
import com.ismt.suitcase.constants.AppConstants
import com.ismt.suitcase.databinding.ActivityAddOrUpdateBinding
import com.ismt.suitcase.room.Product
import com.ismt.suitcase.room.SuitcaseDatabase
import com.ismt.suitcase.utils.BitmapScalar
import java.io.IOException
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class AddOrUpdateActivity : AppCompatActivity() {
    private lateinit var addOrUpdateBinding : ActivityAddOrUpdateBinding
    private var receivedProduct: Product? = null
    private var isForUpdate = false
    private var imageUriPath = ""

    companion object{
        const val RESULT_CODE_COMPLETE = 1001
        const val RESULT_CODE_CANCEL = 1002
        const val GALLERY_PERMISSION_REQUEST_CODE = 11
    }

    private val startCustomCameraActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CustomCameraActivity.CAMERA_ACTIVITY_SUCCESS_RESULT_CODE) {
            imageUriPath = it.data?.getStringExtra(CustomCameraActivity.CAMERA_ACTIVITY_OUTPUT_FILE_PATH)!!
            loadThumbnailImage()
        } else {
            imageUriPath = ""
            addOrUpdateBinding.ivAddImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    // Start gallery activity expecting for result
    private val startGalleryActivityForResult = registerForActivityResult(
        ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            imageUriPath = it.toString()
            contentResolver.takePersistableUriPermission(
                Uri.parse(imageUriPath),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            loadThumbnailImage()
        } else {
            imageUriPath = "";
            addOrUpdateBinding.ivAddImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // Start maps activity expecting for result
    private val startMapActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        //TODO Handle data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Bindings
        addOrUpdateBinding = ActivityAddOrUpdateBinding.inflate(layoutInflater)
        setContentView(addOrUpdateBinding.root)

        // Received data
        receivedProduct = intent.getParcelableExtra<Product>(AppConstants.KEY_PRODUCT)
        receivedProduct?.apply {
            isForUpdate = true
            addOrUpdateBinding.tieTitle.setText(this.title)
            addOrUpdateBinding.tiePrice.setText(this.price)
            addOrUpdateBinding.tieDescription.setText(this.description)
            imageUriPath = this.image.toString()
            addOrUpdateBinding.ivAddImage.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        Uri.parse(this.image)
                    )
//                    bitmap = BitmapScalar.scaleToFill(
//                        bitmap,
//                        addOrUpdateBinding.ivAddImage.width,
//                        addOrUpdateBinding.ivAddImage.height
//                    )
                    addOrUpdateBinding.ivAddImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            //TODO  location
        }

        // On Press of Back Button
        addOrUpdateBinding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_CANCEL, null)
        }

        // On Press of Image
        addOrUpdateBinding.ivAddImage.setOnClickListener {
            handleImageAddButtonClicked()
        }

        // On Maps Select
        addOrUpdateBinding.mbLocation.setOnClickListener {
            startMapActivity()
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

            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-based, so add 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            // Create a string representation of the current date
            val currentDate = "$year-$month-$day"

            // Set The Data In Room Database
            val product = Product(
                title,
                price,
                desc,
                imageUriPath,
                currentDate,
                ""
            )

            // Set the id of the stored product in case of update
            if (isForUpdate) {
                product.id = receivedProduct!!.id
            }

            //Instance of Existing Database
            val suitcaseDatabase = SuitcaseDatabase.getInstance(applicationContext)
            val productDao = suitcaseDatabase.productDao()

            Thread{
                try {
                    if (isForUpdate) {
                        productDao.updateProduct(product)
                        runOnUiThread {
                            //Clear the Input Fields
                            clearInputFields()
                            setResultWithFinish(RESULT_CODE_COMPLETE, product)
                            ToastUtils.showToast(this, "Product Updated!")
                        }
                    } else {
                        // Insert into Product Database
                        productDao.insertNewProduct(product)
                        runOnUiThread {
                            //Clear the Input Fields
                            clearInputFields()
                            setResultWithFinish(RESULT_CODE_COMPLETE, product)
                            ToastUtils.showToast(this, "Product Added!")
                        }
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        ToastUtils.showToast(this, e.toString())
                    }
                }
            }.start()

        }
    }

    // Handel the Pick Image
    private fun handleImageAddButtonClicked() {
        val pickImageBottomSheetDialog = BottomSheetDialog(this)
        pickImageBottomSheetDialog.setContentView(R.layout.bottom_sheet_pick_image)

        val linearLayoutPickByCamera: LinearLayout = pickImageBottomSheetDialog.findViewById(R.id.ll_pick_by_camera)!!
        val linearLayoutPickByGallery: LinearLayout = pickImageBottomSheetDialog.findViewById(R.id.ll_pick_by_gallery)!!

        linearLayoutPickByCamera.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startCameraActivity()
        }
        linearLayoutPickByGallery.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startGalleryToPickImage()
        }

        pickImageBottomSheetDialog.setCancelable(true)
        pickImageBottomSheetDialog.show()
    }

    // Get required permission for camera
    private fun getPermissionsRequiredForCamera(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissions
    }

    // Start the Custom Camera Activity
    private fun startCameraActivity() {
        val intent = Intent(this, CustomCameraActivity::class.java)
        startCustomCameraActivityForResult.launch(intent)
    }

    // Get permission for Gallery
    private fun allPermissionForGalleryGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    // Start the Gallery Pick Image Action
    private fun startGalleryToPickImage() {
        if (allPermissionForGalleryGranted()) {
            startActivityForResultFromGalleryToPickImage()
        } else {
            requestPermissions(
                getPermissionsRequiredForCamera().toTypedArray(),
                GALLERY_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun startActivityForResultFromGalleryToPickImage() {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startGalleryActivityForResult.launch(arrayOf("image/*"))
    }

    // Save the picked image
    private fun loadThumbnailImage() {
        addOrUpdateBinding.ivAddImage.post(Runnable {
            var bitmap: Bitmap?
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    Uri.parse(imageUriPath)
                )
                bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    addOrUpdateBinding.ivAddImage.getWidth(),
                    addOrUpdateBinding.ivAddImage.getHeight()
                )
                addOrUpdateBinding.ivAddImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    // Start Maps Activity
    private fun startMapActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startMapActivityForResult.launch(intent)
    }

    // Send Result code on exiting the page
    private fun setResultWithFinish(resultCode: Int, product: Product?) {
        val intent = Intent()
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        setResult(resultCode, intent)
        finish()
    }

    //Clear input fields data
    private fun clearInputFields(){
        addOrUpdateBinding.tieTitle.text?.clear()
        addOrUpdateBinding.tiePrice.text?.clear()
        addOrUpdateBinding.tieDescription.text?.clear()
    }
}
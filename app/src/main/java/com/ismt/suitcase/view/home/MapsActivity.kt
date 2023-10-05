package com.ismt.suitcase.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.ismt.suitcase.R
import com.ismt.suitcase.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerDragListener
{
    private lateinit var mapsBinding: ActivityMapsBinding
    private var marker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private val fusedLocationProviderClient: FusedLocationProviderClient? = null

    companion object {
        const val MAPS_ACTIVITY_SUCCESS_RESULT_CODE = 3014
        const val MAPS_ACTIVITY_FAILURE_RESULT_CODE = 3015
        const val EXTRA_PRODUCT_LOCATION = "maps_product_location"
        const val EXTRA_MAPS_MESSAGE = "maps_exception_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapsBinding.root)

        val supportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMyLocationClickListener(this)
        this.googleMap.setOnMyLocationButtonClickListener(this)
        this.googleMap.setOnMarkerDragListener(this)
        enableMyLocation()
    }

    override fun onMyLocationClick(location: Location) {
        marker?.apply {
            this.remove()
        }
        locateMarkerToCurrentLocation(location)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMarkerDrag(p0: Marker) {
        this.marker = p0
    }

    override fun onMarkerDragEnd(p0: Marker) {
        this.marker = p0
    }

    override fun onMarkerDragStart(p0: Marker) {
        this.marker = p0
    }

    private fun locateMarkerToCurrentLocation(currentLocation: Location) {
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(currentLatLng)
                .draggable(true)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    private fun locateMarkerToDefaultLocation() {
        val kathmandu = LatLng(27.7172, 85.3240)
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(kathmandu)
                .title("Marker in Kathmandu")
                .draggable(true)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 15f))
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (allPermissionForLocationGranted()) {
            fusedLocationProviderClient?.getCurrentLocation(
                LocationRequest.QUALITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
                        return this
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }
            )?.addOnSuccessListener(OnSuccessListener<Location?> { location ->
                locateMarkerToCurrentLocation(
                    location!!
                )
            })?.addOnFailureListener(
                OnFailureListener { e ->
                    e.printStackTrace()
                    locateMarkerToDefaultLocation()
                })
            googleMap.isMyLocationEnabled = true
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                getPermissionsRequiredForLocation().toTypedArray(),
                101
            )
        }
    }

    private fun allPermissionForLocationGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForLocation()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    private fun getPermissionsRequiredForLocation(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        return permissions
    }

    private fun setResultWithFinish(resultCode: Int) {
        val intent = Intent()
        val latitude = (marker?.position?.latitude).toString()
        val longitude = (marker?.position?.longitude).toString()
        intent.putExtra("location", latitude.plus(longitude))
        finish()
    }
}
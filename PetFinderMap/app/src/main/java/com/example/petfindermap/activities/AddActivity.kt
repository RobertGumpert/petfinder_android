package com.example.petfindermap.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petfindermap.R
import com.example.petfindermap.models.AdCreateHttpModel
import com.example.petfindermap.services.AdService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.add_ad.*
import java.io.IOException
import java.util.*


class AddActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener {

    private var adService: AdService = AdService.getInstance()
    private lateinit var map: GoogleMap
    private lateinit var deviceCurrentLocation: Location
    private var locationAd: LatLng? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_ad)
        super.getSupportActionBar()?.hide()
        addAd()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun toMain(view: View?) {
        val toMain = Intent(this, MapsActivity::class.java)
        startActivity(toMain)
    }

    private fun addAd() {
        buttonAdd.setOnClickListener {
            var type = 2
            if (toggleButtonType.text == "Потерян") type = 1
            val animal_type = editTextType.text.toString()
            val animal_breed = editTextBreed.text.toString()
            val comment_text = editTextComment.text.toString()

            if (animal_type.isEmpty() || animal_breed.isEmpty() || locationAd == null) {
                textView.text = "Заполните поля"
                return@setOnClickListener
            }
            val adCreateHttpModel = AdCreateHttpModel(
                type,
                animal_type,
                animal_breed,
                locationAd!!.latitude,
                locationAd!!.longitude,
                comment_text
            )

            adService.addAd(adCreateHttpModel) {
                if (it != null) {
                    runOnUiThread {
                        val toMain = Intent(this, MapsActivity::class.java)
                        startActivity(toMain)
                    }
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng :LatLng) {
                val location = LatLng(latlng.latitude, latlng.longitude)
                map.clear()
                map.addMarker(MarkerOptions().position(location).draggable(true))
                val titleStr = adService.getAddress(location)
                val editText = findViewById<View>(R.id.editTextAddress) as EditText
                editText.setText(titleStr, TextView.BufferType.EDITABLE);
                locationAd = location

            }
        })
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true)
        googleMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location).draggable(true)
        val titleStr = adService.getAddress(location)
        markerOptions.title(titleStr)


        map.addMarker(markerOptions)
    }



    override fun onMyLocationButtonClick(): Boolean {
        map.clear()
        this.deviceCurrentLocation = map.myLocation
        return false
    }

    override fun onMarkerClick(p0: Marker?) = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }
}
package com.example.petfindermap.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
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
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*


class AddActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener{

    private var adService: AdService = AdService.getInstance()
    private lateinit var map: GoogleMap
    private lateinit var deviceCurrentLocation: Location
    private var locationAd: LatLng? = null
    private val Pick_image_g = 1
    private val Pick_image_c = 2
    private var imageViewPet: ImageView? = null
    private var imageButtonCamera: ImageView? = null
    var camera: Camera? = null
    var image_uri: Uri? = null

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_ad)
        super.getSupportActionBar()?.hide()
        addAd()
        imageViewPet = findViewById(R.id.imageViewPet);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var requestPerm = false
        for (permission in PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPerm = true
            }
        }

        if (requestPerm) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toMain(null)
                }
            }
        }
    }

    fun toGallery(view: View?) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(photoPickerIntent, "Select Picture"),
            Pick_image_g
        );
    }

    fun toCamera(view: View?) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)

        startActivityForResult(intent, Pick_image_c)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        when(requestCode){
            Pick_image_g -> {
                if (resultCode == RESULT_OK) {
                    try {
                        image_uri = imageReturnedIntent!!.data as Uri
                        val imageStream = getContentResolver().openInputStream(image_uri!!) as InputStream
                        val selectedImage = BitmapFactory.decodeStream(imageStream) as Bitmap
                        imageViewPet?.setImageBitmap(selectedImage)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace();
                    }
                }
            }
            Pick_image_c -> {
                if (resultCode == RESULT_OK) {
                    val imageStream = image_uri?.let { getContentResolver().openInputStream(it) } as InputStream
                    val selectedImage = BitmapFactory.decodeStream(imageStream) as Bitmap
                    imageViewPet?.setImageBitmap(selectedImage)
                }
            }
        }

    }

    fun toMain(view: View?) {
        val toMain = Intent(this, MapsActivity::class.java)
        startActivity(toMain)
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
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

            var filePath: String? = null
            if (image_uri != null) {
                filePath = getPath(image_uri)
                image_uri = null
            }
            adService.addAd(adCreateHttpModel, filePath) {
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
        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
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
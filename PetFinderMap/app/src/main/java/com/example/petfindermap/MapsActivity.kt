package com.example.petfindermap


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petfindermap.activities.ActivityListAdverts
import com.example.petfindermap.activities.SignUpActivity
import com.example.petfindermap.adapters.ItemListAdvertAdapter
import com.example.petfindermap.models.AdvertModel
import com.example.petfindermap.services.AdvertService
import com.example.petfindermap.services.UserService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


//
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener {

    private var services: ServiceFacade = ServiceFacade(
        advertService = AdvertService.instance!!,
        userService = UserService.instance!!
    )
    //
    private lateinit var googleMap: GoogleMap
    private lateinit var deviceCurrentLocation: Location
    private var flagOpenListAdverts: Boolean = false
    private lateinit var viewListAdverts: View
    private lateinit var buttonListAdverts: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        entry()
        super.onCreate(savedInstanceState)
        super.getSupportActionBar()?.hide()
        setContentView(R.layout.activity_maps)
        viewListAdverts = findViewById(R.id.list_slide)
        buttonListAdverts = findViewById(R.id.buttonListAdverts)
        viewListAdverts.visibility = View.INVISIBLE
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun entry() {
        if (services.userService.user == null) {
            val signUp = Intent(this, SignUpActivity::class.java)
            startActivity(signUp)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMyLocationButtonClickListener(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            this.googleMap.isMyLocationEnabled = true
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        googleMap.clear()
        this.deviceCurrentLocation = googleMap.myLocation
        findAdvertsInArea()
        return false
    }

    fun listAdvertsSlider(view: View) {
        if (flagOpenListAdverts) {
            viewListAdverts.visibility = View.INVISIBLE
            buttonListAdverts.text = "К списку"
        } else {
            viewListAdverts.visibility = View.VISIBLE
            val adapterListAdverts = ItemListAdvertAdapter(viewListAdverts.context, services.advertService.listFindAdverts)
            val viewListViewAdverts: ListView = findViewById<ListView>(R.id.list_adverts)
            viewListViewAdverts.adapter = adapterListAdverts
            buttonListAdverts.text = "Скрыть"
        }
        flagOpenListAdverts = !flagOpenListAdverts
    }

    private fun findAdvertsInArea() {
        val listAdverts: ArrayList<AdvertModel> = this.services.advertService.searchAdvertInArea(
            this.deviceCurrentLocation.latitude,
            this.deviceCurrentLocation.longitude
        )
        for (advert: AdvertModel in listAdverts) {
            val position = LatLng(advert.GeoLatitude, advert.GeoLongitude)
            val searchAdvertAreaCircle =
                CircleOptions().center(LatLng(position.latitude, position.longitude))
                    .strokeWidth(0F)
            val marker = MarkerOptions().position(position)
            //
            if (advert.AdType == 1) {
                searchAdvertAreaCircle.fillColor(Color.parseColor("#90F6D047"))
                marker.title("Потерян " + advert.CommentText + "'")
                searchAdvertAreaCircle.radius(200.0)
            } else {
                searchAdvertAreaCircle.fillColor(Color.parseColor("#908bf78c"))
                marker.title("Найден " + advert.CommentText + "'")
                searchAdvertAreaCircle.radius(100.0)
            }
            //
            googleMap.addMarker(marker).showInfoWindow()
            googleMap.addCircle(searchAdvertAreaCircle)
        }
    }

}


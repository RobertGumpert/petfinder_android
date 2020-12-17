package com.example.petfindermap.activities


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petfindermap.R
import com.example.petfindermap.adapters.AdsAdapter
import com.example.petfindermap.models.AdModel
import com.example.petfindermap.services.AdService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener {
    private var adService: AdService = AdService.getInstance()

    private lateinit var googleMap: GoogleMap
    private lateinit var deviceCurrentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var flagOpenMenu: Boolean = false
    private var flagOpenListAdverts: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var viewListAdverts: ListView
    private lateinit var buttonMenu: Button
    private lateinit var buttonAddAd: Button
    private lateinit var buttonListAdverts: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getSupportActionBar()?.hide()
        setContentView(R.layout.activity_maps)

        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE
        buttonMenu = findViewById(R.id.buttonMenu)

        buttonAddAd = findViewById(R.id.buttonAddAd)

        viewListAdverts = findViewById(R.id.lvAds)
        viewListAdverts.visibility = View.INVISIBLE
        buttonListAdverts = findViewById(R.id.buttonListAdverts)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }




    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMyLocationButtonClickListener(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            this.googleMap.isMyLocationEnabled = true
        }

        adService.getAds().forEach{
            val markerOptions = MarkerOptions().position(LatLng(it.GeoLatitude, it.GeoLongitude)).draggable(true)
            if(it.typeAd == true){
                markerOptions.
            }
            googleMap.addMarker(markerOptions)
        }



        googleMap.getUiSettings().setZoomControlsEnabled(true)
        googleMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onMyLocationButtonClick(): Boolean {
        googleMap.clear()
        this.deviceCurrentLocation = googleMap.myLocation
        //findAdvertsInArea()
        return false
    }

    fun menuSlider(view: View) {
        if (flagOpenListAdverts) return;
        if (flagOpenMenu) {
            viewMenu.visibility = View.INVISIBLE
            buttonMenu.text = "Меню"
            buttonListAdverts.visibility = View.VISIBLE
        } else {
            viewMenu.visibility = View.VISIBLE
            buttonMenu.text = "Закрыть"
            buttonListAdverts.visibility = View.INVISIBLE
        }
        flagOpenMenu = !flagOpenMenu
    }

    fun listAdvertsSlider(view: View) {
        if (flagOpenMenu) return;
        if (flagOpenListAdverts) {
            viewListAdverts.visibility = View.INVISIBLE
            buttonListAdverts.text = "К списку"
            buttonMenu.visibility = View.VISIBLE
            buttonAddAd.visibility = View.VISIBLE
        } else {
            viewListAdverts.visibility = View.VISIBLE
            val adapterListAdverts = AdsAdapter(
                this,
                adService.getAds()
            )
            viewListAdverts.adapter = adapterListAdverts
            buttonListAdverts.text = "Скрыть"
            buttonMenu.visibility = View.INVISIBLE
            buttonAddAd.visibility = View.INVISIBLE
        }
        flagOpenListAdverts = !flagOpenListAdverts
    }
//
//    private fun findAdvertsInArea() {
//        val listAdverts: ArrayList<AdvertModel>? = this.services.adService?.searchAdvertInArea(
//            this.deviceCurrentLocation.latitude,
//            this.deviceCurrentLocation.longitude
//        )
//        if (listAdverts != null) {
//            for (advert: AdvertModel in listAdverts) {
//                val position = LatLng(advert.GeoLatitude, advert.GeoLongitude)
//                val searchAdvertAreaCircle =
//                    CircleOptions().center(LatLng(position.latitude, position.longitude))
//                        .strokeWidth(0F)
//                val marker = MarkerOptions().position(position)
//                //
//                if (advert.AdType == 1) {
//                    searchAdvertAreaCircle.fillColor(Color.parseColor("#90F6D047"))
//                    marker.title("Потерян " + advert.CommentText + "'")
//                    searchAdvertAreaCircle.radius(200.0)
//                } else {
//                    searchAdvertAreaCircle.fillColor(Color.parseColor("#908bf78c"))
//                    marker.title("Найден " + advert.CommentText + "'")
//                    searchAdvertAreaCircle.radius(100.0)
//                }
//                //
//                googleMap.addMarker(marker).showInfoWindow()
//                googleMap.addCircle(searchAdvertAreaCircle)
//            }
//        }
//    }

    fun menuButton(view: View) {
        when (view.id) {
            R.id.textViewMenuMessages -> {
                val dialogs = Intent(this, DialogsActivity::class.java)
                startActivity(dialogs)
            }
            R.id.textViewMenuMyAds -> {
                val myAds = Intent(this, MyAdsActivity::class.java)
                startActivity(myAds)
            }
        }
    }

    fun addAd(view: View) {
        val addAd = Intent(this, AddActivity::class.java)
        startActivity(addAd)
    }

    fun adItemPress (view: View?) {
        val intent = Intent(this, AdActivity::class.java)
        intent.putExtra("adId", view?.tag.toString())
        intent.putExtra("isMine", (false).toString())
        startActivity(intent)
    }

    override fun onMarkerClick(p0: Marker?) = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }
}


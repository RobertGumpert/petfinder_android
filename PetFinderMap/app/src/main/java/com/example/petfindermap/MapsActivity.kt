package com.example.petfindermap


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petfindermap.activities.ActivityListAdverts
import com.example.petfindermap.activities.SignUpActivity
import com.example.petfindermap.adapters.ItemListAdvertAdapter
import com.example.petfindermap.models.AdvertModel
import com.example.petfindermap.services.AdvertService
import com.example.petfindermap.services.UserService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var deviceCurrentLocation: Location
    private var isUp: Boolean = false
    private lateinit var slideListAdvertsView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        super.getSupportActionBar()?.hide()
        entry()
        //
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //
        slideListAdvertsView = findViewById(R.id.list_slide)
        slideListAdvertsView.visibility = View.INVISIBLE
        //
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

    fun listAdvertsMenu(view: View) {
        val activityListAdverts = Intent(this, ActivityListAdverts::class.java)
        startActivity(activityListAdverts)
    }

    fun listAdvertsSlider(view: View) {
        if (isUp) {
            val animate = TranslateAnimation(
                0F,
                0F,
                0F,
                slideListAdvertsView.height.toFloat()
            )
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
            slideListAdvertsView.visibility = View.INVISIBLE
        } else {
            slideListAdvertsView.visibility = View.VISIBLE
            val animate = TranslateAnimation(
                0F,
                0F,
                slideListAdvertsView.height.toFloat(),
                0F
            )
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
            val customListImplement =
                ItemListAdvertAdapter(slideListAdvertsView.context, services.advertService.listFindAdverts)
            val listView: ListView = findViewById<ListView>(R.id.list_adverts)
            listView.adapter = customListImplement
        }
        isUp = !isUp
    }

    private fun findAdvertsInArea() {
        var listAdverts: ArrayList<AdvertModel> = this.services.advertService.searchAdvertInArea(
            this.deviceCurrentLocation.latitude,
            this.deviceCurrentLocation.longitude
        )
        for (advert: AdvertModel in listAdverts) {
            var position = LatLng(advert.GeoLatitude, advert.GeoLongitude)
            var searchAdvertAreaCircle =
                CircleOptions().center(LatLng(position.latitude, position.longitude))
                    .strokeWidth(0F)
            var marker = MarkerOptions().position(position)
            //
            if (advert.AdType == 1) {
                searchAdvertAreaCircle.fillColor(Color.parseColor("#50f52f2f"))
                marker.title("Потерян " + advert.CommentText + "'")
                searchAdvertAreaCircle.radius(200.0)
            } else {
                searchAdvertAreaCircle.fillColor(Color.parseColor("#507be771"))
                marker.title("Найден " + advert.CommentText + "'")
                searchAdvertAreaCircle.radius(100.0)
            }
            //
            googleMap.addMarker(marker).showInfoWindow()
            googleMap.addCircle(searchAdvertAreaCircle)
        }
    }

}


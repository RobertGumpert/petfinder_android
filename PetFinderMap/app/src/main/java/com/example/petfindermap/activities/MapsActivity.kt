package com.example.petfindermap.activities


import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.petfindermap.adapters.AdsAdapter
import com.example.petfindermap.models.AdModel
import com.example.petfindermap.models.AdsLocationHttpModel
import com.example.petfindermap.R

import com.example.petfindermap.services.AdService
import com.example.petfindermap.services.MapsService
import com.example.petfindermap.services.UserService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.view.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{
    private var adService: AdService = AdService.getInstance()
    private var userService: UserService = UserService.getInstance()

    private lateinit var ads: ArrayList<AdModel>

    private lateinit var googleMap: GoogleMap
    private lateinit var deviceCurrentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var markerAd : Marker
    private var flagOpenMenu: Boolean = false
    private var flagOpenListAdverts: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var viewListAdverts: ListView
    private lateinit var buttonMenu: Button
    private lateinit var buttonAddAd: Button
    private lateinit var buttonListAdverts: Button

    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private var distance : FloatArray = emptyArray<Float>().toFloatArray()


    val NOTIFY_ID: Int = 100;
    val CHANEL_ID = "MyTestApp"
    val CHANEL_NAME = "MyTestAppChannel"
//    private val PROX_ALERT_INTENT = "petfindermap.activities.MapsActivity"
//    private val EXPIRATION = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getSupportActionBar()?.hide()
        setContentView(R.layout.activity_maps)

        adService.geocoderContext = this

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

//        val notificationIntent = Intent(this, MapsActivity::class.java)
//        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//        val notificationChannel: NotificationChannel = NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
//        notificationChannel.description = "Test"
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(notificationChannel)
//        val builder = Notification.Builder(this, CHANEL_ID)
//        builder.setContentIntent(contentIntent)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Уведомление")
//            .setContentText("Тест")
//        notificationManager.notify(NOTIFY_ID, builder.build())
        startService(Intent(this, MapsService::class.java))

    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMyLocationButtonClickListener(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            this.googleMap.isMyLocationEnabled = true
        }

        val adList: ArrayList<AdModel> = arrayListOf()
        if (userService.user == null) return
        adService.getAds(
            AdsLocationHttpModel(
                userService.user!!.user_id,
                true,
                0.0,
                0.0
            )
        ) { adsHttpModel ->
            if (adsHttpModel != null) {
                adList.addAll(adsHttpModel.lost.list)
                adList.addAll(adsHttpModel.found.list)
                ads = arrayListOf()
                ads.addAll(adList.sortedWith(compareBy { it.date_create }))

                runOnUiThread {
                    ads.forEach{
                        Log.d("adInfo", it.toString())
                        val markerOptions = MarkerOptions().position(
                            LatLng(
                                it.geo_latitude,
                                it.geo_longitude
                            )
                        )
                        if(it.ad_type == 1){
                            markerOptions.title("Потерян " + it.animal_type + " " + it.animal_breed)
                            val searchAdvertAreaCircle = CircleOptions().center(
                                LatLng(
                                    it.geo_latitude,
                                    it.geo_longitude
                                )
                            ).strokeWidth(0F)
                            searchAdvertAreaCircle.radius(100.0)
                            searchAdvertAreaCircle.fillColor(Color.parseColor("#90F6D047"))
                            googleMap.addCircle(searchAdvertAreaCircle)
                        }
                        else{
                            markerOptions.title("Найден " + it.animal_type + " " + it.animal_breed)
                            markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_AZURE
                                )
                            )

                        }
                        markerAd = this.googleMap.addMarker(markerOptions)
                        markerAd.setTag(it.ad_id)

                    }
                }
            }

//            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                buildAlertMessageNoGps()
//            }
//            if (checkPermissionForLocation(this)) {
//                startLocationUpdates()
//            }
        }

//        val filter = IntentFilter(PROX_ALERT_INTENT)
//        registerReceiver(ProximityIntentReceiver(), filter)


        this.googleMap.getUiSettings().setZoomControlsEnabled(true)
        this.googleMap.setOnInfoWindowClickListener(this)

        setUpMap()


    }
//
//    private fun addProximityAlert(lat: Double, lng: Double, id: Int) {
//        val intent = Intent(PROX_ALERT_INTENT)
//        val proximityIntent =
//            PendingIntent.getBroadcast(this, id, intent, FLAG_ACTIVITY_NEW_TASK)
//        locationManager.addProximityAlert(lat, lng, POINT_RADIUS, EXPIRATION, proximityIntent)
//    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
                true
            }else{
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have to add startlocationUpdate() method later instead of Toast
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog  = builder.create()
        alert.show()
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }
    fun onLocationChanged(location: Location) {
        // New location has now been determined

//        mLastLocation = location
//        if (mLastLocation != null) {
//            if (userService.user == null) return
//            adService.getAds(
//                AdsLocationHttpModel(
//                    userService.user!!.user_id,
//                    true,
//                    0.0,
//                    0.0
//                )
//            ) { adsHttpModel ->
//                if (adsHttpModel != null) {
//                    adsHttpModel.lost.list.forEach{
//                        Location.distanceBetween(
//                            mLastLocation.latitude,
//                            mLastLocation.longitude, it.geo_latitude,
//                            it.geo_longitude, distance
//                        );
//
//                    }
//                    for (fl in distance) {
//                        if (fl < 1000) {
//
//                            val notificationIntent = Intent(applicationContext, MapsActivity::class.java)
//                            val contentIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//                            val notificationChannel: NotificationChannel = NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
//                            notificationChannel.description = "Test"
//                            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                            notificationManager.createNotificationChannel(notificationChannel)
//                            val builder = Notification.Builder(applicationContext, CHANEL_ID)
//                            builder.setContentIntent(contentIntent)
//                                .setSmallIcon(com.example.petfindermap.R.drawable.ic_launcher_foreground)
//                                .setContentTitle("Уведомление")
//                                .setContentText("Тест")
//                            notificationManager.notify(NOTIFY_ID, builder.build())
//                        }
//                    }
//
//
//                }
//            }
//        }
    }


    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        fusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }


    override fun onInfoWindowClick(marker: Marker?) {
        val ad = ads.find { it.ad_id == marker?.tag}
        if (ad == null) return
        adService.saveAd(ad)
        val intent = Intent(this, AdActivity::class.java)
        intent.putExtra("isMine", (false).toString())
        startActivity(intent)
    }



    override fun onMyLocationButtonClick(): Boolean {

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

            val adList: ArrayList<AdModel> = arrayListOf()
            if (userService.user == null) return
            adService.getAds(
                AdsLocationHttpModel(
                    userService.user!!.user_id,
                    true,
                    0.0,
                    0.0
                )
            ) { adsHttpModel ->
                if (adsHttpModel != null) {
                    adList.addAll(adsHttpModel.lost.list)
                    adList.addAll(adsHttpModel.found.list)
                    ads = arrayListOf()
                    ads.addAll(adList.sortedWith(compareBy { it.date_create }))

                    runOnUiThread {
                        val adapterListAdverts = AdsAdapter(this, ads)
                        viewListAdverts.adapter = adapterListAdverts
                    }
                }
            }
            buttonListAdverts.text = "Скрыть"
            buttonMenu.visibility = View.INVISIBLE
            buttonAddAd.visibility = View.INVISIBLE
        }
        flagOpenListAdverts = !flagOpenListAdverts
    }

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

    fun adItemPress(view: View?) {
        val ad = ads.find { it.ad_id == view?.tag}
        if (ad == null) return
        adService.saveAd(ad)
        val intent = Intent(this, AdActivity::class.java)
        intent.putExtra("isMine", (false).toString())
        startActivity(intent)
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
        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }
}


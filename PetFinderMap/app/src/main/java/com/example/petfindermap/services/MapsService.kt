package com.example.petfindermap.services

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.petfindermap.MainActivity
import com.example.petfindermap.activities.AdActivity
import com.example.petfindermap.activities.MapsActivity
import com.example.petfindermap.models.AdsLocationHttpModel
import java.lang.Math.pow
import kotlin.math.sqrt


class MapsService() : Service(), android.location.LocationListener {

    var lm: LocationManager? = null
    private var userService: UserService = UserService.getInstance()
    private var adService: AdService = AdService.getInstance()
    private val NOTIFY_ID: Int = 100;
    private val CHANEL_ID = "MyTestApp"
    private val CHANEL_NAME = "MyTestAppChannel"
    private val TAG = "BOOMBOOMTESTGPS"
    override fun onBind(arg0: Intent?): IBinder? {
        // TODO Auto-generated method stub
        return null
    }



    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onChanged")
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
                val distances = arrayListOf<Pair<Int, Double>>()
                adsHttpModel.lost.list.forEach{
                    distances.add(
                        Pair(
                            it.ad_id, sqrt(
                                pow(location.latitude - it.geo_latitude, 2.0) + pow(
                                    location.longitude - it.geo_longitude,
                                    2.0
                                )
                            ) * 40000 / 360 * 1000
                        )
                    )
//                    Location.distanceBetween(
//                        location.latitude,
//                        location.longitude,
//                        it.geo_latitude,
//                        it.geo_longitude,
//                        distance
//                    );

                }
                distances.sortBy {
                    it.second
                }
                Log.d("BOOMBOOMTESTGPS", distances.toString())
                if (distances[0].second < 500) {
                    val notificationIntent = Intent(applicationContext, AdActivity::class.java)
                    val ad = adsHttpModel.lost.list.find { it.ad_id == distances[0].first}
                    if (ad != null) {
                        adService.saveAd(ad)
                        notificationIntent.putExtra("isMine", (false).toString())
                        val contentIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                        val notificationChannel: NotificationChannel = NotificationChannel(
                            CHANEL_ID,
                            CHANEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        notificationChannel.description = "Test"
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(notificationChannel)
                        val builder = Notification.Builder(applicationContext, CHANEL_ID)
                        builder.setContentIntent(contentIntent)
                            .setSmallIcon(com.example.petfindermap.R.drawable.ic_launcher_foreground)
                            .setContentTitle("Помогите найти!")
                            .setContentText("Рядом с вами потерялся питомец: " + ad.animal_type)
                            .setAutoCancel(true)
                        notificationManager.notify(NOTIFY_ID, builder.build())
                    }
                }
            }
        }
    }


    override fun onProviderDisabled(provider: String?) {
        // TODO Auto-generated method stub
    }


    override fun onProviderEnabled(provider: String?) {
        // TODO Auto-generated method stub
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // TODO Auto-generated method stub
    }


    override fun onCreate() {
        Log.d(TAG, "onCreate")
        lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        lm!!.requestLocationUpdates(lm!!.getBestProvider(Criteria(), true), 5 * 60000, 0f, this)
    }


//
//    private val TAG = "BOOMBOOMTESTGPS"
//
//    private var mLocationManager: LocationManager? = null
//    private var LOCATION_INTERVAL : Long = 1000
//    private var LOCATION_DISTANCE = 10f
////    var pendingIntent : PendingIntent
////    init {
////        var mapIntent = Intent(this, MapsActivity::class.java)
////
////        pendingIntent = PendingIntent.getActivity(applicationContext, 0, mapIntent, 0)
////
////    }
////val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//    override fun onLocationChanged(location: Location?) {
//        if (location!=null) {
//            lathitude.setText(String.valueOf(location.getLatitude()));
//            longitude.setText(String.valueOf(location.getLongitude()));
//        }
//        else{
//            lathitude.setText("Sorry, location");
//            longitude.setText("unavailable");
//        }
//    }
//    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//    override fun onProviderEnabled(provider: String?) {}
//    override fun onProviderDisabled(provider: String?) {}
//
//
//    private class LocationListener(provider: String, var applicationContext: Context) : android.location.LocationListener {
//        private var userService: UserService = UserService.getInstance()
//        private var adService: AdService = AdService.getInstance()
//        private lateinit var distance : FloatArray
//        val NOTIFY_ID: Int = 100;
//        val CHANEL_ID = "MyTestApp"
//        val CHANEL_NAME = "MyTestAppChannel"
//
//
//        var mLastLocation: Location
//        override fun onLocationChanged(location: Location) {
//            mLastLocation.set(location)
////            if (userService.user == null) return
////            adService.getAds(
////                AdsLocationHttpModel(
////                    userService.user!!.user_id,
////                    true,
////                    0.0,
////                    0.0
////                )
////            ) { adsHttpModel ->
////                if (adsHttpModel != null) {
////                    adsHttpModel.lost.list.forEach{
////                        Location.distanceBetween(
////                            mLastLocation.latitude,
////                            mLastLocation.longitude, it.geo_latitude,
////                            it.geo_longitude, distance
////                        );
////
////                    }
////                    for (fl in distance) {
////                        if (fl < 1000) {
////
////                            val notificationIntent = Intent(applicationContext, MapsActivity::class.java)
////                            val contentIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
////                            val notificationChannel: NotificationChannel = NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
////                            notificationChannel.description = "Test"
////                            notificationManager.createNotificationChannel(notificationChannel)
////                            val builder = Notification.Builder(applicationContext, CHANEL_ID)
////                            builder.setContentIntent(contentIntent)
////                                .setSmallIcon(com.example.petfindermap.R.drawable.ic_launcher_foreground)
////                                .setContentTitle("Уведомление")
////                                .setContentText("Тест")
////                            notificationManager.notify(NOTIFY_ID, builder.build())
////                        }
////                    }
////
////
////                }
////            }
//
//        }
//
//        override fun onProviderDisabled(provider: String) {
//        }
//
//        override fun onProviderEnabled(provider: String) {
//        }
//
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
//        }
//
//        init {
//            mLastLocation = Location(provider)
//        }
//    }
//
//    private var mLocationListeners = arrayOf(
//        LocationListener(LocationManager.GPS_PROVIDER, applicationContext),
//        LocationListener(LocationManager.NETWORK_PROVIDER, applicationContext)
//    )
//
//    override fun onBind(arg0: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        return START_STICKY
//    }
//
//    override fun onCreate() {
//        Log.e(TAG, "onCreate")
//        initializeLocationManager()
//        try {
//            mLocationManager?.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                LOCATION_INTERVAL,
//                LOCATION_DISTANCE,
//                mLocationListeners[1]
//            )
//        } catch (ex: SecurityException) {
//            Log.i(TAG, "fail to request location update, ignore", ex)
//        } catch (ex: IllegalArgumentException) {
//            Log.d(TAG, "network provider does not exist, " + ex.message)
//        }
//        try {
//            mLocationManager?.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                LOCATION_INTERVAL,
//                LOCATION_DISTANCE,
//                mLocationListeners[0]
//            )
//        } catch (ex: SecurityException) {
//            Log.i(TAG, "fail to request location update, ignore", ex)
//        } catch (ex: IllegalArgumentException) {
//            Log.d(TAG, "gps provider does not exist " + ex.message)
//        }
//
//
//
//    }
//
//    override fun onDestroy() {
//        Log.e(TAG, "onDestroy")
//        super.onDestroy()
//        if (mLocationManager != null) {
//            for (i in 0 until mLocationListeners.size) {
//                try {
//                    mLocationManager!!.removeUpdates(mLocationListeners[i])
//                } catch (ex: Exception) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex)
//                }
//            }
//        }
//    }
//
//    private fun initializeLocationManager() {
//        Log.e(TAG, "initializeLocationManager")
//        if (mLocationManager == null) {
//            mLocationManager =
//                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        }
//    }

}
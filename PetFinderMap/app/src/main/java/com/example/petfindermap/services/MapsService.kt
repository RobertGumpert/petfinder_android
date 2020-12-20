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
                adsHttpModel.lost.list.forEach {
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
                }
                distances.sortBy {
                    it.second
                }
                if (distances[0].second < 500) {
                    val notificationIntent = Intent(applicationContext, AdActivity::class.java)
                    val ad = adsHttpModel.lost.list.find { it.ad_id == distances[0].first }
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
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
        lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        lm!!.requestLocationUpdates(lm!!.getBestProvider(Criteria(), true), 5000, 0f, this)
    }
}
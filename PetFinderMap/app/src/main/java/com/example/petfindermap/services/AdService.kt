package com.example.petfindermap.services

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.petfindermap.HttpManager
import com.example.petfindermap.R
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.Ad
import com.example.petfindermap.models.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AdService {
//    private val appDatabase : AppDatabase = AppDatabase.getInstance()
    private val httpManager : HttpManager = HttpManager.getInstance()
    private val userService: UserService = UserService.getInstance()
    private val gson : Gson = Gson()

    private var adInfo: AdModel? = null

    lateinit var geocoderContext: Context

    companion object {
        private var instance: AdService? = null
        fun getInstance(): AdService {
            if (instance == null){
                instance = AdService()
            }
            return instance!!
        }
    }


    fun addAd(adCreateHttpModel: AdCreateHttpModel, filePath: String?, callback: (AdModel?)-> Unit) {
        val postBody = gson.toJson(adCreateHttpModel)
        httpManager.queryFormData(
            "ad",
            "/api/advert/user/add",
            postBody,
            filePath,
            listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token)))
        { code: Int, body: String ->
            when (code) {
                200 -> {
                    val info = gson.fromJson(body, AdModel::class.java)
                    callback(info)
                }
                else -> {
                    callback(null)
                }
            }
        }
    }

    fun getMyAds(callback: (AdsHttpModel?)-> Unit) {
        httpManager.query("ad","/api/advert/user/list", null, listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token))) { code: Int, body: String ->
            when (code) {
                200 -> {
                    val info = gson.fromJson(body, AdsHttpModel::class.java)
                    callback(info)
                }
                else -> {
                    callback(null)
                }
            }
        }
    }

    fun getAds(adsLocationHttpModel: AdsLocationHttpModel, callback: (AdsHttpModel?)-> Unit) {
        val postBody = gson.toJson(adsLocationHttpModel)
        httpManager.query("ad","/api/advert/get/in/area", postBody, listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token))) { code: Int, body: String ->
            when (code) {
                200 -> {
                    val info = gson.fromJson(body, AdsHttpModel::class.java)
                    callback(info)
                }
                else -> {
                    callback(null)
                }
            }
        }
    }

    fun saveAd(adInfo: AdModel) {
        this.adInfo = adInfo
    }

    fun getAd() : AdModel? {
        return adInfo
    }

    fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(geocoderContext)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                if(address.locality != null){
                    addressText += address.locality
                }
                if(address.thoroughfare != null){
                    addressText += ", " + address.thoroughfare
                }

                if(address.subThoroughfare != null){
                    addressText += ", " + address.subThoroughfare
                }
            }
        } catch (e: IOException) {
            Log.e("AddActivity", e.localizedMessage)
        }

        return addressText
    }

    fun createComplaint(adId: Long) {

    }
}
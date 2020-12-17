package com.example.petfindermap.models

import java.util.*

data class AdModel(
    val id: Int,
    val userName: String,
    val typeAd: Boolean,
    val pet: String,
    val name: String,
    val breed: String,
    val address: String,
    val GeoLatitude: Double,
    val GeoLongitude: Double,
    var date: Date,
    val image: Int,
    val comment: String)

//class AdModel(
//    val ad_owner_id: Int,
//    val ad_owner_name: String,
//    val ad_type: Int,
//    val ad_id: Int,
//    val animal_type: String,
//    val animal_breed: String,
//    val geo_latitude: Double,
//    val geo_longitude: Double,
//    val comment_text: String,
//    val image_url: String,
//    var date_create: Date,
//    var date_close: Date
//)
package com.example.petfindermap.models

import java.util.*

class AdModel(
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
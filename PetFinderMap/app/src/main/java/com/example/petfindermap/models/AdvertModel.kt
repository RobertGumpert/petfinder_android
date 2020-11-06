package com.example.petfindermap.models

import java.util.*

data class AdvertModel(
    var AdID: Int,
    var AdOwnerID: Int?,
    var AdType: Int?,
    var AnimalType: String?,
    var AnimalBreed: String?,
    var GeoLatitude: Double,
    var GeoLongitude: Double,
    var CommentText: String?,
    var DateCreate: Date?,
    var DateClose: Date?
)
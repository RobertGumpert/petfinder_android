package com.example.petfindermap.models

import java.util.*

data class AdCreateHttpModel(
    val ad_type: Int,
    val animal_type: String,
    val animal_breed: String,
    val geo_latitude: Double,
    val geo_longitude: Double,
    val comment_text: String
)
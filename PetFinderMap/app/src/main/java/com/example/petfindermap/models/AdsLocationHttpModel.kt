package com.example.petfindermap.models

import java.util.*

data class AdsLocationHttpModel(
    val ad_owner_id: Int,
    val only_not_closed: Boolean,
    val geo_longitude: Double,
    val geo_latitude: Double
)
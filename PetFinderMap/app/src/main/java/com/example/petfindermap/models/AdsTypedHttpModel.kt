package com.example.petfindermap.models

import java.util.*
import kotlin.collections.ArrayList

data class AdsTypedHttpModel(
    val list: ArrayList<AdModel>,
    val expire: ArrayList<AdModel>
)
package com.example.petfindermap.models

import java.util.*

data class AdsHttpModel(
    val lost: AdsTypedHttpModel,
    val found: AdsTypedHttpModel
)
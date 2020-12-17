package com.example.petfindermap.models

import java.util.*

data class MessageHttpModel(
    val dialog_id: Int,
    val user_receiver_id: Int,
    val message_id: Int,
    val user_id: Int,
    val user_name: String,
    val text: String,
    val date_create: Date
)
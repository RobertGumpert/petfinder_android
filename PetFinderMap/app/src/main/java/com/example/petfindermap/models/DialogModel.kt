package com.example.petfindermap.models

data class DialogModel(
    val dialog_id: Int,
    val dialog_name: String,
    val user_receiver_id: Int,
    val skip_messages: Int,
    val messages: ArrayList<MessageModel>
)
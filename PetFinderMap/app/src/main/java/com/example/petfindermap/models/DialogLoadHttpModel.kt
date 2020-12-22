package com.example.petfindermap.models

data class DialogLoadHttpModel(
    val dialog_id: Int,
    val next_skip: Int,
    val user_receiver_id: Int,
    val messages: ArrayList<MessageModel>
)
package com.example.petfindermap.models

class DialogModel(
    val id: Int,
    val name: String,
    val avatarUrl: String,
    val messages: List<MessageModel>?
)
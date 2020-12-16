package com.example.petfindermap.models

data class UserModel(
    var user_id: Int,
    var telephone: String,
    var email: String,
    var name: String,
    var avatar_url: String,
    var access_token: String
)
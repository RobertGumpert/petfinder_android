package com.example.petfindermap.models

data class UserResetPasswordHttpModel (
    var reset_token: String,
    var password: String
)
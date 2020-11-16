package com.example.petfindermap

import com.example.petfindermap.services.AdvertService
import com.example.petfindermap.services.UserService

data class ServiceFacade(
    var advertService: AdvertService?,
    var userService: UserService?
)
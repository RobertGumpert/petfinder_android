package com.example.petfindermap

import com.example.petfindermap.services.AdService
import com.example.petfindermap.services.AdvertService
import com.example.petfindermap.services.DialogsService
import com.example.petfindermap.services.UserService

data class ServiceFacade(
    var advertService: AdvertService?,
    var userService: UserService?,
    var dialogsService: DialogsService?,
    var adService: AdService?
)
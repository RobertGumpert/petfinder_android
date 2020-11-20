package com.example.petfindermap

import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.services.AdvertService
import com.example.petfindermap.services.DialogsService
import com.example.petfindermap.services.UserService

data class ServiceFacade(
    var advertService: AdvertService?,
    var userService: UserService?,
    var dialogsService: DialogsService?
)
package com.example.petfindermap.services

import com.example.petfindermap.models.AdvertModel

class AdvertService {

    companion object {
        var instance: AdvertService? = null
            get() {
                if (field == null) {
                    instance = AdvertService()
                }
                return field
            }
    }

    var listFindAdverts = arrayListOf(
        AdvertModel(
            AdID = 1,
            CommentText = "Собака Рэкс",
            GeoLatitude = 54.329407,
            GeoLongitude = 48.397301,
            AnimalBreed = "Собака",
            AnimalType = "Овчарка",
            AdOwnerID = 1,
            AdType = 1,
            DateClose = null,
            DateCreate = null
        ),
        AdvertModel(
            AdID = 1,
            CommentText = "Кошка Машка",
            GeoLatitude = 54.328812,
            GeoLongitude = 48.397930,
            AnimalBreed = "Кошка",
            AnimalType = "Британская",
            AdOwnerID = 1,
            AdType = 2,
            DateClose = null,
            DateCreate = null
        )
    )

    fun searchAdvertInArea(GeoLatitude: Double, GeoLongitude: Double): ArrayList<AdvertModel> {
        //
        listFindAdverts[0].GeoLatitude = GeoLatitude + 0.001
        listFindAdverts[0].GeoLongitude = GeoLongitude
        //
        listFindAdverts[1].GeoLatitude = GeoLatitude
        listFindAdverts[1].GeoLongitude = GeoLongitude + 0.001
        //
        return listFindAdverts
    }

}
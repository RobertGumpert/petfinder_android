package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.R
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.Ad
import com.example.petfindermap.models.AdModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class AdService(context: Context) {
    var appDatabase : AppDatabase? = AppDatabase.getInstance(context)

    companion object {
        var instance: AdService? = null
        fun getInstance(context: Context): AdService? {
            if (instance == null){
                instance = AdService(context)
            }
            return instance
        }
    }


    fun addAd(Type: Boolean, Pet: String, Name: String, Breed: String, Address: String, Date: Long, Comment: String) {

        if (Pet.isEmpty() || Breed.isEmpty() || Name.isEmpty() || Address.isEmpty()) {
            throw java.lang.Exception("Non valid data.")
        }
        runBlocking {
            val writer = GlobalScope.launch {
                val ads = appDatabase?.adDao()?.getAll()
                var id = 1
                if (ads?.size != 0) {
                    id = ads?.last()?.adId?.plus(1)!!
                }

                appDatabase?.adDao()?.insert(
                    Ad(
                        adId = id,
                        userName = "Виктория Дмитриева",
                        typeAd = Type,
                        name = Name,
                        pet = Pet,
                        breed = Breed,
                        address = Address,
                        date = Date,
                        imageUrl = "",
                        comment = Comment
                    )
                )
            }
            writer.join()
        }
    }

    fun getAds(): ArrayList<AdModel> {
        val myAdListItems : ArrayList<AdModel> = ArrayList()
        runBlocking {
            val reader = GlobalScope.launch {
                val list = appDatabase?.adDao()?.getAll()
                if (list != null) {
                    list.forEach {
                        myAdListItems.add(
                            AdModel(
                                it.adId,
                                it.userName,
                                it.typeAd,
                                it.pet,
                                it.name,
                                it.breed,
                                it.address,
                                Date(it.date),
                                R.drawable.dog,
                                it.comment
                            )
                        )
                    }
                }
            }
            reader.join()
        }
        return myAdListItems
    }

    fun getAd(id: Long) : AdModel? {
        var adModel : AdModel? = null
        runBlocking {
            val reader = GlobalScope.launch {
                val ad: Ad? = appDatabase?.adDao()?.getById(id)
                if (ad != null) {
                    adModel = AdModel(
                        ad.adId,
                        ad.userName,
                        ad.typeAd,
                        ad.pet,
                        ad.name,
                        ad.breed,
                        ad.address,
                        Date(ad.date),
                        R.drawable.dog,
                        ad.comment
                    )
                }
            }
            reader.join()
        }
        return adModel
    }
}
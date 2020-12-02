package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.R
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.Ad
import com.example.petfindermap.models.MyAdModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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


    fun addAd(Type: String, Pet: String, Name: String, Breed: String, Address: String, Date: String, Comment: String) {

        if (Type.isEmpty() || Pet.isEmpty() || Breed.isEmpty() || Name.isEmpty() || Address.isEmpty() || Date.isEmpty()) {
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

    fun getAds(): ArrayList<MyAdModel> {
        val myAdListItems : ArrayList<MyAdModel> = ArrayList()
        runBlocking {
            val reader = GlobalScope.launch {
                val list = appDatabase?.adDao()?.getAll()
                if (list != null) {
                    list.forEach {
                        myAdListItems.add(
                            MyAdModel(
                                it.name,
                                it.address,
                                R.drawable.dog
                            )
                        )
                    }
                }
            }
            reader.join()
        }
        return myAdListItems
    }
}
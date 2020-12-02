package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.Ad
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
                var id = appDatabase?.adDao()?.getAll()?.last()?.adId?.plus(1)
                if(id != null){
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

            }
            writer.join()
        }
    }
}
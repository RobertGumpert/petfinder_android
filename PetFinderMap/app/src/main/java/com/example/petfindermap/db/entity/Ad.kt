package com.example.petfindermap.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ad (
    @PrimaryKey val adId: Int,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "type_ad") val typeAd: String,
    @ColumnInfo(name = "pet") val pet: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "breed") val breed: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "comment") val comment: String
    )
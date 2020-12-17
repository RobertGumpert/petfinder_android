package com.example.petfindermap.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petfindermap.db.dao.AdDao
import com.example.petfindermap.db.dao.DialogDao
import com.example.petfindermap.db.dao.UserDao
import com.example.petfindermap.db.entity.Ad
import com.example.petfindermap.db.entity.Dialog
import com.example.petfindermap.db.entity.User

@Database(entities = arrayOf(User::class, Dialog::class, Ad::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dialogDao(): DialogDao
    abstract fun adDao(): AdDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null){
                synchronized(AppDatabase::class){
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "petfinder_db_4")
                        .build()
                }
            }
            return instance!!
        }

        fun getInstance(): AppDatabase {
            return instance!!
        }

        fun destroyDataBase() {
            instance = null
        }
    }
}
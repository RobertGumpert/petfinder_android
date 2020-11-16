package com.example.petfindermap.db.dao

import androidx.room.*
import com.example.petfindermap.db.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE userId = :id")
    fun getById(id: Long): User?

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}
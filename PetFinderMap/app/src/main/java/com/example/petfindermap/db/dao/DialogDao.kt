package com.example.petfindermap.db.dao

import androidx.room.*
import com.example.petfindermap.db.entity.Dialog


@Dao
interface DialogDao {
    @Query("SELECT * FROM dialog")
    fun getAll(): List<Dialog>

    @Query("SELECT * FROM dialog WHERE dialogId = :id")
    fun getById(id: Long): Dialog?

    @Insert
    fun insert(dialog: Dialog)

    @Update
    fun update(dialog: Dialog)

    @Delete
    fun delete(dialog: Dialog)
}
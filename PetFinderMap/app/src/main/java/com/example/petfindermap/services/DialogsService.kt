package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.HttpManager
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.*
import com.example.petfindermap.models.*
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class DialogsService {
    val appDatabase : AppDatabase = AppDatabase.getInstance()
    val httpManager : HttpManager = HttpManager.getInstance()
    val userService: UserService = UserService.getInstance()
    val gson : Gson = Gson()

    companion object {
        private var instance: DialogsService? = null
        fun getInstance(): DialogsService {
            if (instance == null){
                instance = DialogsService()
            }
            return instance!!
        }
    }

    fun getDialogsMessages(callback: (ArrayList<DialogModel>?)-> Unit) {
        httpManager.query("di","/api/user/dialog/get", null, listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token))) { code: Int, body: String ->
            when (code) {
                200 -> {
                    val info = gson.fromJson(body, DialogsHttpModel::class.java)
                    callback(info.dialogs)
                }
                401 -> {
                    userService.refreshAccessToken {
                        callback(null)
                    }
                }
                else -> {
                    callback(null)
                }
            }
        }
    }

    fun createDialog(userId: Int, dialogName: String, callback: (Int?)-> Unit) {
        val dialogCreateHttpModel = DialogCreateHttpModel(userId,  dialogName)
        val postBody = gson.toJson(dialogCreateHttpModel)
        httpManager.query("di","/api/user/dialog/create", postBody, listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token))) { code: Int, body: String ->
            when (code) {
                200 -> {
                    val info = gson.fromJson(body, NumberHttpModel::class.java)
                    callback(info.id)
                }
            }
        }
    }

    fun sendMessage(dialogId: Int, text: String, callback: (MessageHttpModel?)-> Unit) {
        val sendMessageHttpModel = SendMessageHttpModel(dialogId,  text)
        val postBody = gson.toJson(sendMessageHttpModel)
        httpManager.query("di","/api/user/message/send", postBody, listOf(Pair("Authorization", "Bearer " + userService.user!!.access_token))) { code: Int, body: String ->
            if (code == 200) {
                val info = gson.fromJson(body, MessageHttpModel::class.java)
                callback(info)
            }
        }
    }
}
package com.example.petfindermap.services

import android.content.Context
import com.example.petfindermap.db.AppDatabase
import com.example.petfindermap.db.entity.*
import com.example.petfindermap.models.DialogModel
import com.example.petfindermap.models.MessageModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class DialogsService(context: Context) {
    var listDialogs: ArrayList<DialogModel> = ArrayList()

    var appDatabase : AppDatabase = AppDatabase.instance!!

    companion object {
        var instance: DialogsService? = null
        fun getInstance(context: Context): DialogsService? {
            if (instance == null){
                instance = DialogsService(context)
            }
            return instance
        }
    }

    fun loadDialogsMessages() {
        runBlocking {
            val writer = GlobalScope.launch {
                var list = appDatabase.dialogDao().getAll()
                if (list.isEmpty()) {
                    listDialogs = fillData()
                    var i = 1
                    listDialogs.forEach{
                        appDatabase.dialogDao().insert(
                            Dialog(
                                i,
                                it.name,
                                it.avatarUrl
                            )
                        )
                        i++
                    }
                } else {
                    list.forEach{
                        listDialogs.add(DialogModel(
                            it.dialogId,
                            it.name,
                            it.avatarUrl,
                            getMessagesByDialogId(it.dialogId)))
                    }
                }
            }
            writer.join()
        }
    }

    fun getMessagesByDialogId(id: Int): List<MessageModel>? {
        var list = fillData()
        return list.find { element -> element.id == id }?.messages
    }

    fun fillData(): ArrayList<DialogModel> {
        var names = arrayOf(
            "Кузнецов Влад",
            "Максин Илья",
            "Шарафутдинова Виктория",
            "Аникьев Данил"
        )
        var messages = arrayOf(
            "Привет!",
            "Как дела?",
            "Отлично",
            "Чем занимаешься?",
            "Отдыхаю",
            "А ты?",
            "Нормас",
            "Живем..."
        )
        var listDialogs = ArrayList<DialogModel>()
        for (i in 0..(names.size - 1)) {
            var messages = arrayListOf(
                MessageModel(
                    messages[i * 2],
                    (i * 2) % 2 == 0,
                    Date(),
                    (i * 2) % 2 == 0
                ),
                MessageModel(
                    messages[i * 2 + 1],
                    (i * 2 + 1) % 2 == 0,
                    Date(),
                    (i * 2 + 1) % 2 == 0
                )
                )

            listDialogs.add(
                DialogModel(
                    i + 1,
                    names[i],
                    "",
                    messages
                    )
            )
        }
        return listDialogs
    }
}
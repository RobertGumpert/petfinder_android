package com.example.petfindermap.services

import com.example.petfindermap.R
import com.example.petfindermap.models.DialogModel

class DialogsService {
    var listDialogs: ArrayList<DialogModel> = fillData()

    companion object {
        var instance: DialogsService? = null
            get() {
                if (field == null) {
                    instance = DialogsService()
                }
                return field
            }
    }

    // генерируем данные для адаптера
    fun fillData(): ArrayList<DialogModel> {
        var listDialogs = ArrayList<DialogModel>()
        for (i in 1..20) {
            listDialogs.add(
                DialogModel(
                    "Путин В. В.",
                    R.drawable.ic_baseline_account_circle_24
                )
            )
        }
        return listDialogs
    }
}
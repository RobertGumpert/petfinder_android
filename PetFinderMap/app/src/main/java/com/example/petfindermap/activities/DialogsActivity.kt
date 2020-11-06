package com.example.petfindermap.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.DialogsAdapter
import com.example.petfindermap.models.DialogModel

class DialogsActivity : AppCompatActivity() {
    var dialogItems: ArrayList<DialogModel> = ArrayList()
    lateinit var dialogsAdapter: DialogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        super.getSupportActionBar()?.hide()

        fillData()
        dialogsAdapter = DialogsAdapter(this, dialogItems)

        val lvMain: ListView = findViewById(R.id.lvDialogs) as ListView
        lvMain.setAdapter(dialogsAdapter)
    }

    // генерируем данные для адаптера
    fun fillData() {
        for (i in 1..20) {
            dialogItems.add(
                DialogModel(
                    "Путин В. В.",
                    R.drawable.ic_baseline_account_circle_24
                )
            )
        }
    }
}
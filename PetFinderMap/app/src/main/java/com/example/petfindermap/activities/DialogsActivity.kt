package com.example.petfindermap.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.DialogsAdapter
import com.example.petfindermap.models.DialogModel
import com.example.petfindermap.services.DialogsService

class DialogsActivity : AppCompatActivity() {
    var dialogItems: ArrayList<DialogModel> = ArrayList()
    private var dialogsService: DialogsService = DialogsService.instance!!
    lateinit var dialogsAdapter: DialogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        super.getSupportActionBar()?.hide()

        dialogsAdapter = DialogsAdapter(this, dialogsService.listDialogs)

        val lvMain: ListView = findViewById(R.id.lvDialogs) as ListView
        lvMain.setAdapter(dialogsAdapter)
    }
}
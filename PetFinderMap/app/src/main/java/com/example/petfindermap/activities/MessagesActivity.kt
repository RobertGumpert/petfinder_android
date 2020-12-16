package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.MessagesAdapter
import com.example.petfindermap.services.DialogsService

class MessagesActivity : AppCompatActivity() {
    private var dialogsService: DialogsService = DialogsService.getInstance()
    lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages)
        super.getSupportActionBar()?.hide()


        val dialogId = intent.getStringExtra("dialogId").toInt()
        val dialog = dialogsService.getDialogsMessages().find { element -> element.id == dialogId }
        if (dialog != null) {
            val textViewName: TextView = findViewById(R.id.textViewName)
            textViewName.text = dialog.name
            messagesAdapter = MessagesAdapter(this, dialog.messages)
            val lvMain: ListView = findViewById(R.id.lvMessages)
            lvMain.adapter = messagesAdapter
        }
    }

    fun goToDialogs(view: View) {
        val dialogs = Intent(this, DialogsActivity::class.java)
        startActivity(dialogs)
    }


}
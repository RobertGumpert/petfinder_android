package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.example.petfindermap.R
import com.example.petfindermap.adapters.MessagesAdapter
import com.example.petfindermap.models.MessageModel
import com.example.petfindermap.services.DialogsService
import com.example.petfindermap.services.UserService

class MessagesActivity : AppCompatActivity() {
    private val dialogsService: DialogsService = DialogsService.getInstance()
    private val userService: UserService = UserService.getInstance()
    lateinit var messagesAdapter: MessagesAdapter

    private var dialogId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages)
        super.getSupportActionBar()?.hide()


        dialogId = intent.getStringExtra("dialog_id").toInt()
        dialogsService.getDialogsMessages() {
            val dialog = it?.find { element -> element.dialog_id == dialogId }
            if (dialog != null) {
                runOnUiThread {
                    val textViewName: TextView = findViewById(R.id.textViewName)
                    var dialogName = dialog.dialog_name
                    if (dialog.dialog_name.length > 16) {
                        val dialogNameArgs = dialog.dialog_name.split(' ')
                        dialogName = dialogNameArgs[0] + " " + dialogNameArgs[1][0] + "."
                    }
                    textViewName.text = dialogName
                    messagesAdapter = MessagesAdapter(this, dialog.messages, userService.user!!.user_id)
                    val lvMain: ListView = findViewById(R.id.lvMessages)
                    lvMain.adapter = messagesAdapter
                }
            }
        }
    }

    fun goToDialogs(view: View) {
        val dialogs = Intent(this, DialogsActivity::class.java)
        startActivity(dialogs)
    }

    fun sendMessage(view: View) {
        val editText: EditText = findViewById(R.id.editTextMessage)
        val message = editText.text.toString()
        editText.setText("")

        dialogsService.sendMessage(dialogId, message) {
            runOnUiThread {
                if (it != null) {
                    messagesAdapter.addItem(
                        MessageModel(
                            it.message_id,
                            it.dialog_id,
                            it.user_receiver_id,
                            it.user_name,
                            it.text,
                            it.date_create
                        )
                    )
                }
            }
        }
    }
}
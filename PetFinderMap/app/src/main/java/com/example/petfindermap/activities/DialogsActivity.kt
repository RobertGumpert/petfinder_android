package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.MapsActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.DialogsAdapter
import com.example.petfindermap.models.DialogModel
import com.example.petfindermap.services.DialogsService

class DialogsActivity : AppCompatActivity() {
    private var dialogsService: DialogsService = DialogsService.instance!!
    lateinit var dialogsAdapter: DialogsAdapter

    private var flagOpenMenu: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var buttonMenu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        super.getSupportActionBar()?.hide()

        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE
        buttonMenu = findViewById(R.id.buttonMenu)

        dialogsService.loadDialogsMessages()
        dialogsAdapter = DialogsAdapter(this, dialogsService.listDialogs)
        val lvMain: ListView = findViewById(R.id.lvDialogs) as ListView
        lvMain.setAdapter(dialogsAdapter)
    }

    fun menuSlider(view: View) {
        if (flagOpenMenu) {
            viewMenu.visibility = View.INVISIBLE
            buttonMenu.text = "Меню"
        } else {
            viewMenu.visibility = View.VISIBLE
            buttonMenu.text = "Закрыть"
        }
        flagOpenMenu = !flagOpenMenu
    }

    fun menuButton(view: View) {
        when (view.id) {
            R.id.textViewMenuMap -> {
                val map = Intent(this, MapsActivity::class.java)
                startActivity(map)
            }
        }
    }
}
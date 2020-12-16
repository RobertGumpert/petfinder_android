package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.AdService
import kotlinx.android.synthetic.main.add_ad.*
import java.util.*

class AddActivity : AppCompatActivity() {

    private var adService: AdService = AdService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_ad)
        super.getSupportActionBar()?.hide()
        addAd()
    }

    fun toMain (view: View?) {
        val toMain = Intent(this, MapsActivity::class.java)
        startActivity(toMain)
    }

    private fun addAd() {
        buttonAdd.setOnClickListener {
            var type = false
            if (toggleButtonType.text == "Потерян") type = true
            val pet = editTextPet.text.toString()
            val name = editTextName.text.toString()
            val breed = editTextBreed.text.toString()
            val address = editTextAddress.text.toString()
            val date = calendarView.date
            val comment = editTextComment.text.toString()
            try {
                adService.addAd(
                    Type = type,
                    Pet = pet,
                    Name = name,
                    Breed = breed,
                    Address = address,
                    Date = date,
                    Comment = comment
                )
            } catch (ex: java.lang.Exception) {
                textView.text = "Ошибка добавления"
                return@setOnClickListener
            }
            finish()
            val toMain = Intent(this, MapsActivity::class.java)
            startActivity(toMain)
        }

    }
}
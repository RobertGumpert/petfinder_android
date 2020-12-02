package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.MapsActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.AdService
import kotlinx.android.synthetic.main.add_ad.*

class AddActivity : AppCompatActivity() {

    private var adService: AdService = AdService.instance!!

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
            val type = toggleButtonType.text.toString()
            val pet = editTextPet.text.toString()
            val name = editTextName.text.toString()
            val breed = editTextBreed.text.toString()
            val address = editTextAddress.text.toString()
            val date = calendarView.date.toString()
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
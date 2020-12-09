package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.MapsActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.AdService

class AdActivity : AppCompatActivity() {
    private var adService: AdService = AdService.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad)
        super.getSupportActionBar()?.hide()

        val adId = intent.getStringExtra("adId").toLong()

        val ad = adService.getAd(adId)
        if (ad == null) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        else {
            val textViewType: TextView  = findViewById(R.id.textViewType)
            var typeText = "Потерян!"
            if (!ad.typeAd)  {
                typeText = "Найден!"
            }
            textViewType.text = typeText

            val textViewPet: TextView  = findViewById(R.id.textViewPet)
            textViewPet.text = ad.pet + ", " + ad.name + ", " + ad.breed

            val textViewName: TextView  = findViewById(R.id.textViewName)
            textViewName.text = "Владелец: " + ad.userName

            val textViewDate: TextView  = findViewById(R.id.textViewDate)
            textViewDate.text = "Потерялся: " + ad.date.toString()

            val textViewAddress: TextView  = findViewById(R.id.textViewAddress)
            textViewAddress.text = ad.address

            val textViewComment: TextView  = findViewById(R.id.textViewComment)
            textViewComment.text = ad.comment
        }
    }

    fun toMain (view: View?) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.AdService

class ComplaintActivity : AppCompatActivity() {
    private var adService: AdService = AdService.instance!!
    private var adId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complaint)
        super.getSupportActionBar()?.hide()

        val adId = intent.getStringExtra("adId").toLong()
        this.adId = adId
    }

    fun toAd (view: View?) {
        val intent = Intent(this, AdActivity::class.java )
        intent.putExtra("adId", adId.toString())
        intent.putExtra("isMine", (false).toString())
        startActivity(intent)
    }

    fun sendComplaint (view: View?) {
        adService.createComplaint(adId)
        toAd(view)
    }
}
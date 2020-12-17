package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.AdsAdapter
import com.example.petfindermap.adapters.DialogsAdapter
import com.example.petfindermap.models.AdModel
import com.example.petfindermap.services.AdService

class MyAdsActivity : AppCompatActivity() {
    private lateinit var listAdapter: AdsAdapter
    private var adService: AdService = AdService.getInstance()

    private var flagOpenMenu: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var buttonMenu: Button

    private lateinit var ads: ArrayList<AdModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_ads)
        super.getSupportActionBar()?.hide()

        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE
        buttonMenu = findViewById(R.id.buttonMenu)

        var adList: ArrayList<AdModel> = arrayListOf()
        adService.getMyAds() { adsHttpModel ->
            if (adsHttpModel != null) {
                adList.addAll(adsHttpModel.lost.list)
                adList.addAll(adsHttpModel.found.list)
                ads = arrayListOf()
                ads.addAll(adList.sortedWith(compareBy { it.date_create }))

                runOnUiThread {
                    listAdapter = AdsAdapter(this, ads)
                    val lvMyList: ListView = findViewById(R.id.lvAds) as ListView
                    lvMyList.setAdapter(listAdapter)
                }
            }
        }
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
                val maps = Intent(this, MapsActivity::class.java)
                startActivity(maps)
            }
            R.id.textViewMenuMessages -> {
                val dialogs = Intent(this, DialogsActivity::class.java)
                startActivity(dialogs)
            }
        }
    }

    fun adItemPress (view: View?) {
        val ad = ads.find { it.ad_id == view?.tag}
        if (ad == null) return
        adService.saveAd(ad)
        val intent = Intent(this, AdActivity::class.java)
        intent.putExtra("isMine", (true).toString())
        startActivity(intent)
    }
}
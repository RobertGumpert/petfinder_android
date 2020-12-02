package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.MapsActivity
import com.example.petfindermap.R
import com.example.petfindermap.adapters.MyAdsAdapter
import com.example.petfindermap.services.AdService

class MyAdsActivity : AppCompatActivity() {
    lateinit var myListAdapter: MyAdsAdapter
    private var adService: AdService = AdService.instance!!

    private var flagOpenMenu: Boolean = false
    private lateinit var viewMenu: View
    private lateinit var buttonMenu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_ads)
        super.getSupportActionBar()?.hide()

        viewMenu = findViewById(R.id.menu_slide)
        viewMenu.visibility = View.INVISIBLE
        buttonMenu = findViewById(R.id.buttonMenu)

        myListAdapter = MyAdsAdapter(this, adService.getAds())

        val lvMyList: ListView = findViewById(R.id.lvAds) as ListView
        lvMyList.setAdapter(myListAdapter)
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
            R.id.textViewMenuMyAds -> {
                val myAds = Intent(this, MyAdsActivity::class.java)
                startActivity(myAds)
            }
        }
    }

    fun ad (view: View?) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
package com.example.petfindermap.activities

import com.example.petfindermap.R
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.adapters.ItemListAdvertAdapter
import com.example.petfindermap.services.AdvertService


class ActivityListAdverts: AppCompatActivity() {

    private var advertService: AdvertService = AdvertService.instance!!
    private lateinit var customListImplement : ItemListAdvertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_list_adverts)
        //
        customListImplement = ItemListAdvertAdapter(this, advertService.listFindAdverts)
        val listView: ListView = findViewById<ListView>(R.id.list_adverts)
        listView.adapter = customListImplement
    }
}
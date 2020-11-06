package com.example.petfindermap.adapters

import android.annotation.SuppressLint
import com.example.petfindermap.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.petfindermap.models.AdvertModel


class ItemListAdvertAdapter : BaseAdapter {

    private var context: Context
    private var layout: LayoutInflater
    lateinit var adverts: ArrayList<AdvertModel>

    constructor(context: Context, adverts: ArrayList<AdvertModel>?) {
        this.context = context
        if (adverts != null) {
            this.adverts = adverts
        }
        this.layout = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = layout.inflate(R.layout.item_list_advert, parent, false)
        }
        //
        var advert = getAdvert(position)
        (view!!.findViewById<View>(R.id.title) as TextView).text = advert.AnimalBreed + ", " + advert.AnimalType
        if (advert.AdType == 1) {
            (view.findViewById<View>(R.id.type) as TextView).text = "Потерянное животное"
        } else {
            (view.findViewById<View>(R.id.type) as TextView).text = "Найденное животное"
        }
        //
        return view
    }

    private fun getAdvert(position: Int): AdvertModel {
        return getItem(position) as AdvertModel
    }

    override fun getItem(position: Int): Any {
        return adverts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return adverts.size
    }
}
package com.example.petfindermap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.petfindermap.R
import com.example.petfindermap.models.MyAdModel

class MyAdsAdapter (val context: Context?, val objects: ArrayList<MyAdModel>?) : BaseAdapter() {
    var lInflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    override fun getCount(): Int {
        return objects!!.size
    }

    override fun getItem(position: Int): Any {
        return objects!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View? = convertView
        if (view == null) {
            view = lInflater?.inflate(R.layout.my_ads_item, parent, false)
        }
        val p: MyAdModel = getList(position)

        (view?.findViewById(R.id.tvName) as TextView).text = p.name
        (view?.findViewById(R.id.tvAddress) as TextView).text = p.address
        (view.findViewById(R.id.ivPetImage) as ImageView).setImageResource(p.image)

        return view
    }

    private fun getList(position: Int): MyAdModel {
        return getItem(position) as MyAdModel
    }
}
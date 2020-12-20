package com.example.petfindermap.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.petfindermap.HttpManager
import com.example.petfindermap.R
import com.example.petfindermap.models.AdModel
import com.example.petfindermap.services.AdService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

class AdsAdapter (val context: Context?, val objects: ArrayList<AdModel>?) : BaseAdapter() {
    var lInflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    private var adService: AdService = AdService.getInstance()

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
            view = lInflater?.inflate(R.layout.ads_item, parent, false)
        }
        val p: AdModel = getList(position)

        runBlocking {
            val downloaderPhotoFile = GlobalScope.launch {
                val updateUrl = p.image_url.replace(
                    oldValue = "127.0.0.1",
                    newValue = HttpManager.getInstance().IP_ADDR_DEVICE
                )
                val url = URL(updateUrl)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                (view?.findViewById(R.id.ivPetImage) as ImageView).setImageBitmap(bmp)
            }
            downloaderPhotoFile.join()
        }
        (view?.findViewById(R.id.tvName) as TextView).text = p.animal_type + " " + p.animal_breed
        (view?.findViewById(R.id.tvAddress) as TextView).text = adService.getAddress(LatLng(p.geo_latitude, p.geo_longitude))
        view.tag = p.ad_id
        return view
    }

    private fun getList(position: Int): AdModel {
        return getItem(position) as AdModel
    }
}
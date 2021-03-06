package com.example.petfindermap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.petfindermap.R
import com.example.petfindermap.models.DialogModel


class DialogsAdapter(val context: Context?, val objects: List<DialogModel>) : BaseAdapter() {
    var lInflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): Any {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View? = convertView
        if (view == null) {
            view = lInflater?.inflate(R.layout.dialogs_item, parent, false)
        }
        val p: DialogModel = getDialog(position)

        (view?.findViewById(R.id.tvName) as TextView).text = p.dialog_name
        (view.findViewById(R.id.ivUserImage) as ImageView).setImageResource(R.drawable.ic_baseline_account_circle_24)
        view.tag = p.dialog_id
        return view
    }

    private fun getDialog(position: Int): DialogModel {
        return getItem(position) as DialogModel
    }
}
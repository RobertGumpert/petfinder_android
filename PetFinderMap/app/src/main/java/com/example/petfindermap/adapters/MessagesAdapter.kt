package com.example.petfindermap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.petfindermap.R
import com.example.petfindermap.models.DialogModel
import com.example.petfindermap.models.MessageModel

class MessagesAdapter(val context: Context?, val objects: List<MessageModel>) : BaseAdapter() {
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
        val p: MessageModel = getMessage(position)

        var view: View? = convertView
        if (view == null) {
            var messageType = R.layout.messages_item;
            if (p.isMine) {
                messageType = R.layout.messages_item;
            }
            view = lInflater?.inflate(messageType, parent, false)
        }

        (view?.findViewById(R.id.textViewMessage) as TextView).text = p.text


        return view
    }

    private fun getMessage(position: Int): MessageModel {
        return getItem(position) as MessageModel
    }
}
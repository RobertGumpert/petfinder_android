package com.example.petfindermap.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.petfindermap.R
import com.example.petfindermap.models.DialogModel
import com.example.petfindermap.models.MessageModel

class MessagesAdapter(val context: Context?, val objects: ArrayList<MessageModel>, val user_id: Int, val loadMessages: ()-> Unit) : BaseAdapter() {
    var lInflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    var isLoadAllMessages = false

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
            view = lInflater?.inflate(R.layout.messages_item, parent, false)
        }

        val textViewMessage = (view?.findViewById(R.id.textViewMessage) as TextView)
        textViewMessage.text = p.text
        if (p.user_id == user_id) {
            Log.d("MessagesAdapter", "My: " + p.text)
//            if (textViewMessage.textAlignment != TextView.TEXT_ALIGNMENT_TEXT_END) {
//                textViewMessage.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
//            }
            (view as ConstraintLayout).setBackgroundResource(R.drawable.my_message_item_background)
        }
        else {
            Log.d("MessagesAdapter", "Not my: " + p.text)
//            if (textViewMessage.textAlignment != TextView.TEXT_ALIGNMENT_TEXT_START) {
//                textViewMessage.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
//            }
            (view as ConstraintLayout).setBackgroundResource(R.drawable.dialog_item_background)
        }

        if (position == 0 && !isLoadAllMessages) {
            loadMessages()
        }

        return view
    }

    private fun getMessage(position: Int): MessageModel {
        return getItem(position) as MessageModel
    }

    fun addItem(item: MessageModel) {
        objects.add(item)
        notifyDataSetChanged()
    }

    fun addFirstItems(items: ArrayList<MessageModel>) {
        if (items.size == 0) {
            isLoadAllMessages = true
        }
        else {
            objects.addAll(0, items)
            notifyDataSetChanged()
        }
    }
}
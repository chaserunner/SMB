package com.example.shoppinglist

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val listTitle = itemView?.findViewById<TextView>(R.id.itemString) as TextView
    val checkBox = itemView?.findViewById<CheckBox>(R.id.checkBox) as CheckBox


}
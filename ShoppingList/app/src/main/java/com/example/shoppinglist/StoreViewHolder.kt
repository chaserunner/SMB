package com.example.shoppinglist

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val listTitle = itemView?.findViewById<TextView>(R.id.titleTextView) as TextView
    val locationTitle = itemView?.findViewById<TextView>(R.id.locationTextView) as TextView
    val radiusTitle = itemView?.findViewById<TextView>(R.id.radiusTextView) as TextView
    val checkBox = itemView?.findViewById<CheckBox>(R.id.checkBox) as CheckBox
}
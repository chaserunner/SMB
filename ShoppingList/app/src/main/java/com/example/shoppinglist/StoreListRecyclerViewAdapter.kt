package com.example.shoppinglist

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_view_holder.view.*


class StoreListRecyclerViewAdapter(val list: List<Store>,
                                   val clickListener: ListSelectionRecyclerViewClickListener,
                                   val sharedPref: SharedPreferences)
    : RecyclerView.Adapter<StoreViewHolder>() {

    interface ListSelectionRecyclerViewClickListener {
        fun storeSelected(item: Store, selected: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.store_view_holder,
                parent,
                false) // 2
        return StoreViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        if (holder != null) {
            val store = list[position]
            holder.checkBox.isChecked = store.isFavorite
            holder.listTitle.text =  store.name + " - " + store.description
            holder.locationTitle.text = "Location: (lon: ${store.location.longitude}, lat: ${store.location.latitude})"
            holder.radiusTitle.text = "Radius: ${store.radius}m"
            holder.itemView.checkBox.setOnCheckedChangeListener({ button, isChecked ->
                clickListener.storeSelected(store,isChecked)
            })

            if (sharedPref.getBoolean(MainActivity.BIG_FONT, false)) {
                holder.listTitle.textSize = 20.0f
            } else {
                holder.listTitle.textSize = 14.0f
            }
        }
    }
}
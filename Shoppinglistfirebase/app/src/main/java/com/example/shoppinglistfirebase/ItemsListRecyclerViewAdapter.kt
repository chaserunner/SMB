package com.example.shoppinglistfirebase

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_view_holder.view.*


class ItemsListRecyclerViewAdapter(val list: ArrayList<Item>,
                                   val clickListener: ListSelectionRecyclerViewClickListener,
                                   val sharedPref: SharedPreferences)
    : RecyclerView.Adapter<ItemViewHolder>() {

    interface ListSelectionRecyclerViewClickListener {
        fun listItemClicked(item: Item)
        fun listItemLongPressed(item: Item)
        fun itemSelected(item: Item, selected: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_view_holder,
                parent,
                false) // 2
        return ItemViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder != null) {
            val item = list[position]
            holder.checkBox.isChecked = item.isBought
            holder.listTitle.text =  "${item.count.toString()} x ${item.name} (${(item.price * item.count).toString()})"
            holder.itemView.setOnClickListener({
                clickListener.listItemClicked(item)
            })
            holder.itemView.setOnLongClickListener {
                clickListener.listItemLongPressed(item)
                true
            }
            holder.itemView.checkBox.setOnCheckedChangeListener({ button, isChecked ->
                clickListener.itemSelected(item,isChecked)
            })

            if (sharedPref.getBoolean(MainActivity.BIG_FONT, false)) {
                holder.listTitle.textSize = 20.0f
            } else {
                holder.listTitle.textSize = 14.0f
            }
        }
    }
}
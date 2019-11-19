package com.example.shoppinglist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val recievedItem: Item? = intent.getParcelableExtra(MainActivity.INTENT_ITEM_KEY)
        item = recievedItem ?: Item("",0.0, 1, false)
        title = recievedItem?.name ?: "Create item"
        titleField.setText(item.name)
        priceField.setText(item.price.toString())
        quantityView.text = item.count.toString()

        plusButton.setOnClickListener {
            item.count++
            quantityView.text = item.count.toString()
        }

        minusButton.setOnClickListener {
            if (item.count > 1) {
                item.count--
                quantityView.text = item.count.toString()
            }
        }
        
        saveButton.setOnClickListener {
            item.name = titleField.text.toString()
            item.price = priceField.text.toString().toDouble()
            finishActivity(true)
        }
    }

    fun finishActivity(save: Boolean) {

        val bundle = Bundle()
        if (item.name != "" && save) {
            bundle.putParcelable(MainActivity.INTENT_ITEM_KEY, item)
        }
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity(false)
    }
}

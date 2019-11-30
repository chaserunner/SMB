package com.example.shoppinglistfirebase

import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import com.example.shoppinglistfirebase.ItemsListRecyclerViewAdapter as ItemsListRecyclerViewAdapter
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.preference.PreferenceManager


class ListActivity : AppCompatActivity(),
    ItemsListRecyclerViewAdapter.ListSelectionRecyclerViewClickListener {

    companion object {
        val INTENT_ITEM_KEY = "item"
        val REQUEST_CODE =  123
        val DARK_MODE = "DARK_MODE"
        val BIG_FONT = "BIG_FONT"
    }

    //lateinit var database: AppDatabase

    lateinit var itemsRecyclerView: RecyclerView

    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        //database = Room.databaseBuilder(this, AppDatabase::class.java,
        //    "item-master-db").allowMainThreadQueries().build()
        itemsRecyclerView = findViewById(R.id.items_recyclerview)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        reloadData()
        fab.setOnClickListener { view ->
            showItemDetail()
        }

        intent.getStringExtra("itemName")?.let { itemName: String ->
//            val allItems = database.itemCategoryDao().getAll()
//            val index = allItems.indexOfFirst { it.name == itemName }
//            if (index != -1) {
//                showItemDetail(allItems[index])
//            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (sharedPref.getBoolean(DARK_MODE, false)) {
            itemsRecyclerView.setBackgroundColor(Color.GRAY)
        } else {
            itemsRecyclerView.setBackgroundColor(Color.WHITE)
        }

        reloadData()
    }

    fun setActivityBackgroundColor(color: Int) {
        val view = this.window.decorView
        view.setBackgroundColor(color)
    }

    fun reloadData() {
        //val list = ArrayList(database.itemCategoryDao().getAll())
        //itemsRecyclerView.adapter = ItemsListRecyclerViewAdapter(list,this, sharedPref)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        openSettings()
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openSettings(){
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    private fun showItemDetail(item: Item? = null) {
        // 1
        val listDetailIntent = Intent(this, ItemActivity::class.java)
        // 2
        listDetailIntent.putExtra(INTENT_ITEM_KEY, item)
        // 3
        startActivityForResult(listDetailIntent, REQUEST_CODE)
    }

    override fun listItemClicked(item: Item) {
        showItemDetail(item)
    }

    override fun listItemLongPressed(item: Item) {
        //database.itemCategoryDao().delete(item)
        reloadData()
    }

    override fun itemSelected(item: Item, selected: Boolean) {
        item.isBought = selected
        //database.itemCategoryDao().update(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 1
        if (requestCode == REQUEST_CODE) {
// 2
            data?.let {

                val item: Item? = data.getParcelableExtra(INTENT_ITEM_KEY)
                item?.let {
//                    if (database.itemCategoryDao().getAll().indexOfFirst { it.name == item.name } != -1) {
//                        database.itemCategoryDao().update(item)
//                    } else {
//                        database.itemCategoryDao().insertAll(item)
//                        Intent().also { intent ->
//                            intent.setAction("android.intent.shoppinglist.itemadded")
//                            intent.putExtra("itemName", item.name)
//                            sendBroadcast(intent,"com.shoppinglist.CREATEITEM")
//                        }
//                    }
                    reloadData()
                }
            }
        }
    }
}

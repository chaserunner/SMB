package com.example.shoppinglist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import com.example.shoppinglist.ItemsListRecyclerViewAdapter as ItemsListRecyclerViewAdapter
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.database.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(),
    ItemsListRecyclerViewAdapter.ListSelectionRecyclerViewClickListener {

    companion object {
        val INTENT_ITEM_KEY = "item"
        val TAG = "Main activity database"
        val REQUEST_CODE =  123
        val DARK_MODE = "DARK_MODE"
        val BIG_FONT = "BIG_FONT"
        val DATABASE_REF = "items"
        val RC_SIGN_IN = 321
    }

    lateinit var fDatabase: DatabaseReference

    lateinit var itemsRecyclerView: RecyclerView

    lateinit var sharedPref: SharedPreferences

    var currentItems = mutableMapOf<String, Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)


        itemsRecyclerView = findViewById(R.id.items_recyclerview)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        val self = this
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            startMonitoring(user.uid)
        } else {
            showLoginScreen()
        }

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

    fun startMonitoring(userID: String) {
        val self = this
        fDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_REF).child(userID)
        fDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currentItems.clear()
                val list = mutableListOf<Item>()
                dataSnapshot.children.mapNotNullTo(list) { it.getValue<Item>(Item::class.java) }
                list.forEach {
                    currentItems[it.name] = it
                }
                itemsRecyclerView.adapter = ItemsListRecyclerViewAdapter(list, self, sharedPref)
                Log.d(TAG, "Value is: $list")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (sharedPref.getBoolean(DARK_MODE, false)) {
            itemsRecyclerView.setBackgroundColor(Color.GRAY)
        } else {
            itemsRecyclerView.setBackgroundColor(Color.WHITE)
        }
    }

    fun setActivityBackgroundColor(color: Int) {
        val view = this.window.decorView
        view.setBackgroundColor(color)
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
        if (item.itemId == R.id.action_settings) {
            openSettings()
        } else if  (item.itemId == R.id.action_logout) {
           logout()
        }

        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logout -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openSettings(){
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                showLoginScreen()
            }
    }

    fun showLoginScreen() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

// Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
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
        currentItems.remove(item.name)
        fDatabase.setValue(currentItems)
    }

    override fun itemSelected(item: Item, selected: Boolean) {
        item.isBought = selected
        currentItems[item.name] = item
        fDatabase.setValue(currentItems)
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
                    currentItems[item.name] = item
                    fDatabase.setValue(currentItems)
                }
            }
        }

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                startMonitoring(user?.uid ?: "")
                Log.d("asdasd", "Successful login")
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}

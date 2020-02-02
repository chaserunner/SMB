package com.example.shoppinglist

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StoreList : AppCompatActivity(),
 StoreListRecyclerViewAdapter.ListSelectionRecyclerViewClickListener {

    companion object {
        val STORE_REFERENCE = "stores"
    }

    lateinit var geofencingClient: GeofencingClient

    lateinit var fDatabase: DatabaseReference

    lateinit var storesRecyclerView: RecyclerView

    lateinit var sharedPref: SharedPreferences

    private var uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_list)

        title = "Store List"
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        storesRecyclerView = findViewById(R.id.store_recycler_view)
        storesRecyclerView.layoutManager = LinearLayoutManager(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        // Required if your app targets Android 10 or higher.
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_BACKGROUND_LOCATION")
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                "android.permission.ACCESS_BACKGROUND_LOCATION")) {

                ActivityCompat.requestPermissions(this,
                    arrayOf("android.permission.ACCESS_BACKGROUND_LOCATION"),
                    1234)
            }

        } else {
            startMonitoring(uid)
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun storeSelected(item: Store, selected: Boolean) {
        item.isFavorite = selected
        fDatabase.child(item.id).setValue(item)
    }

    fun startMonitoring(userID: String) {
        val self = this
        fDatabase = FirebaseDatabase.getInstance().getReference(STORE_REFERENCE).child(userID)
        fDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<Store>()
                dataSnapshot.children.mapNotNullTo(list) { it.getValue<Store>(Store::class.java) }
                if (list.isEmpty()) {
                    populateData()
                } else {
                    storesRecyclerView.adapter =
                        StoreListRecyclerViewAdapter(list, self, sharedPref)

                }
                Log.d(MainActivity.TAG, "Value is: $list")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(MainActivity.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun setupGeofences(list: List<Store>) {
        val favourites = list.filter { it.isFavorite }
        val geofences = favourites.map {
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("$STORE_REFERENCE/$uid/${it.id}")

                // Set the circular region of this geofence.
                .setCircularRegion(
                    it.location.latitude,
                    it.location.longitude,
                    it.radius.toFloat()
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(1000 * 60 * 60 * 24 * 30)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build()
        }.mapNotNull { it }
        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofences)
        }.build()
        geofencingClient.removeGeofences(geofencePendingIntent)
        geofencingClient.addGeofences(request,geofencePendingIntent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {
            startMonitoring(uid)
        }
    }

    private fun populateData() {
        val storesList = listOf<Store>(
            Store("Biedronka",
                Location(52.241917,20.994400),
                100,
                "Sklep spożywczy"),
            Store("Arkadia",
                Location(52.256108, 20.983952),
                100,
                "Centrum handlowy"),
            Store("Żabka",
                Location(52.225129, 20.991814),
                100,
                "Sklep spożywczy"),
            Store("IKEA",
                Location(52.305646, 21.082984),
                100,
                "Sklep meblowy"),
            Store("Factory Annopol",
                Location(52.299173, 21.025088),
                100,
                "Outlet store")
        ).map { it.id to it }.toMap()
        fDatabase.setValue(storesList)
    }
}

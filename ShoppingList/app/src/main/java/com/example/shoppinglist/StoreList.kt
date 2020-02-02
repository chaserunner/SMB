package com.example.shoppinglist

import android.Manifest
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
        val REQUEST_LOCATION = 7436
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
           // if (permissionRationaleAlreadyShown) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION)
//            } else {
////                // Show an explanation to the user as to why your app needs the
////                // permission. Display the explanation *asynchronously* -- don't block
////                // this thread waiting for the user's response!
////            }
        } else {
            startMonitoring(uid)
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
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
                    setupGeofences(list)
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
                .setRequestId("$STORE_REFERENCE/$uid/${it.id}/name")

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
            setInitialTrigger(0)
            addGeofences(geofences)
        }.build()
        Log.d("Geofence", request.toString())
        geofencingClient.removeGeofences(geofencePendingIntent)
        geofencingClient.addGeofences(request,geofencePendingIntent)
            .addOnSuccessListener {
               Log.d("Geofence", "success")
            }
            // 4
            .addOnFailureListener {
                Log.d("Geofence", "error")
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOCATION) {
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

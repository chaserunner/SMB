package com.example.shoppinglist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StoreMap : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var fDatabase: DatabaseReference

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun startMonitoring() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val self = this
        fDatabase = FirebaseDatabase.getInstance().getReference(StoreList.STORE_REFERENCE).child(userID)
        fDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<Store>()
                val filtered = dataSnapshot.children.mapNotNullTo(list) { it.getValue<Store>(Store::class.java) }.filter { it.isFavorite }
                filtered.forEach {
                    map.addMarker(MarkerOptions().position(LatLng(it.location.latitude, it.location.longitude))
                        .title(it.name + " (" + it.description + ")" ))
                }
                val margin = 0.03
                val minlat = (filtered.map { it.location.latitude }.min() ?: 0.0) - margin
                val maxlat = (filtered.map { it.location.latitude }.max() ?: 0.0) + margin
                val minlon = (filtered.map { it.location.longitude }.min() ?: 0.0) - margin
                val maxlon = (filtered.map { it.location.longitude }.max() ?: 0.0) + margin
                val bounds = LatLngBounds(LatLng(minlat ,minlon), LatLng(maxlat, maxlon))
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1))
                //map.moveCamera(CameraUpdateFactory.zoomBy(0.8.toFloat()))
               // map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                Log.d(MainActivity.TAG, "Value is: $list")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(MainActivity.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        startMonitoring()
    }
}

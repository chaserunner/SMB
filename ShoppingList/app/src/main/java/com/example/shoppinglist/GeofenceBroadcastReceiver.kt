package com.example.shoppinglist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        val TAG = "GeofenceBroadcastReceiver"
        val CHANNEL_ID = "my.channel"
    }
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "received trasition")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
//            val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this,
//                geofenceTransition,
//                triggeringGeofences
//            )
            triggeringGeofences.forEach {
                FirebaseDatabase.getInstance().getReference(it.requestId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.getValue(String::class.java)
                        var builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                            .setContentTitle(if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "Entered store" else "Leaved store")
                            .setContentText(name)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)

                        with(NotificationManagerCompat.from(context!!)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(UUID.randomUUID().hashCode(), builder.build())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                    }
                })
            }

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails)

        } else {
            // Log the error.
//            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
//                geofenceTransition))
        }
    }
}
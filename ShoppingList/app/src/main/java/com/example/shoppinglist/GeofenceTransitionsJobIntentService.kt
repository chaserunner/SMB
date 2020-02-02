package com.example.shoppinglist

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
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

class GeofenceTransitionsJobIntentService : JobIntentService() {

    companion object {

        private const val JOB_ID = 573
        val LOG_TAG = "geofence service"
        val CHANNEL_ID = "channel"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        // 1
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        // 2
        if (geofencingEvent.hasError()) {
            val errorMessage = "error occured"
            Log.e(LOG_TAG, errorMessage)
            return
        }
        // 3
        handleEvent(geofencingEvent)
    }

    fun handleEvent(geofencingEvent: GeofencingEvent) {
        Log.d(LOG_TAG, "received trasition")
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(LOG_TAG, errorMessage)
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

            triggeringGeofences.forEach {
                FirebaseDatabase.getInstance().getReference(it.requestId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.getValue(String::class.java)
                        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                            .setContentTitle(if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "Entered store" else "Leaved store")
                            .setContentText(name)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)

                        with(NotificationManagerCompat.from(applicationContext)) {
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

        }
    }
}
package com.example.broadcastrecieverproject2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.R
import android.app.Notification
import android.app.PendingIntent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver(val parentContext: Context) : BroadcastReceiver() {

    companion object {
        val CHANNEL_ID = "my.channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "recieved intent")
        val itemName = intent.getStringExtra("itemName")
        Log.d(TAG, itemName)

        // Create an explicit intent for an Activity in your app
        val intent = context.packageManager.getLaunchIntentForPackage("com.example.shoppinglist")
        intent.putExtra("itemName", itemName)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(parentContext, 0, intent, 0)

        var builder = NotificationCompat.Builder(parentContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(itemName)
            .setContentText("was added to list")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(parentContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(itemName.hashCode(), builder.build())
        }
    }
}
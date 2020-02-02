package com.example.shoppinglist

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.shoppinglist.MediaPlayerService.Companion.NEXT_ACTION
import com.example.shoppinglist.MediaPlayerService.Companion.PREV_ACTION

/**
 * Implementation of App Widget functionality.
 */
class ShoppingWidget : AppWidgetProvider() {


    lateinit var views: RemoteViews

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            // Construct the RemoteViews object
            views = RemoteViews(context.packageName, R.layout.shopping_widget)


            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("https://www.github.com")

            val pending = PendingIntent.getActivity(
                context, 0,
                intent, 0
            )

            views.setOnClickPendingIntent(R.id.button2, pending)

            // PLAY
            val intentPlay = Intent(context, MediaPlayerService::class.java)
            intentPlay.action = MediaPlayerService.ACTION_PLAY
            intentPlay.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0, intentPlay, 0
            )

            views.setOnClickPendingIntent(R.id.play, pendingIntentPlay)

            // PREV
            val prevSound = Intent(context, MediaPlayerService::class.java)
            prevSound.action = MediaPlayerService.PREV_ACTION
            prevSound.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingPrevSound = PendingIntent.getBroadcast(
                context,
                0, prevSound, 0
            )

            views.setOnClickPendingIntent(R.id.previous, pendingPrevSound)

            // NEXT
            val nextSound = Intent(context, MediaPlayerService::class.java)
            nextSound.action = MediaPlayerService.NEXT_ACTION
            nextSound.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingNextSound = PendingIntent.getBroadcast(
                context,
                0, nextSound, 0
            )

            views.setOnClickPendingIntent(R.id.next, pendingNextSound)



            // PREV IMAGE IN IMAGE VIEW
            val intentPrev = Intent(context, ShoppingWidget::class.java)
            intentPrev.action = "com.example.widgetapp.prevPhoto"
            intentPrev.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingIntentPrev = PendingIntent.getBroadcast(
                context,
                0, intentPrev, 0
            )


            views.setOnClickPendingIntent(R.id.app_widget_actionButton, pendingIntentPrev)


            // NEXT IMAGE IN IMAGE VIEW
            val intentNext = Intent(context, ShoppingWidget::class.java)
            intentNext.action = "com.example.widgetapp.nextPhoto"
            intentNext.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingIntentNext = PendingIntent.getBroadcast(
                context,
                0, intentNext, 0
            )

            views.setOnClickPendingIntent(R.id.app_widget_actionButtonNext, pendingIntentNext)


            views.setImageViewResource(R.id.imageViewForPhoto, R.drawable.meme1)


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {

        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        views = RemoteViews(context.packageName, R.layout.shopping_widget)

        if (intent.action == NEXT_ACTION) {

         //   views.setImageViewResource(R.id.imageViewForPhoto, R.drawable.meme1)

            val mManager = AppWidgetManager.getInstance(context)

            val cn = ComponentName(
                context,
                ShoppingWidget::class.java
            )

            mManager.updateAppWidget(cn, views)
        }
        if (intent.action == PREV_ACTION) {

          //  views.setImageViewResource(R.id.imageViewForPhoto, R.drawable.meme1)

            val mManager = AppWidgetManager.getInstance(context)

            val cn = ComponentName(
                context,
                ShoppingWidget::class.java
            )

            mManager.updateAppWidget(cn, views)
        }
        if (intent.action == "com.example.widgetapp.prevPhoto") {

            views.setImageViewResource(R.id.imageViewForPhoto, R.drawable.meme1)

            val mManager = AppWidgetManager.getInstance(context)

            val cn = ComponentName(
                context,
                ShoppingWidget::class.java
            )

            mManager.updateAppWidget(cn, views)
        }

        if (intent.action == "com.example.widgetapp.nextPhoto") {

            views.setImageViewResource(R.id.imageViewForPhoto, R.drawable.meme2)

            val mManager = AppWidgetManager.getInstance(context)

            val cn = ComponentName(
                context,
                ShoppingWidget::class.java
            )

            mManager.updateAppWidget(cn, views)

        }

    }
}


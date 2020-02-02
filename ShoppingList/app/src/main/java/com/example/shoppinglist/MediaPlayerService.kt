package com.example.shoppinglist

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log


class MediaPlayerService: Service(), MediaPlayer.OnPreparedListener {

    companion object {
        val ACTION_PLAY: String = "com.example.action.PLAY"
        val NEXT_ACTION = "next_action"
        val PREV_ACTION = "prev_action"
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Media", "bind")
     return null
    }

    private var mMediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("Media", "start")
        val action: String = intent.action
        when(action) {
            ACTION_PLAY -> {
                mMediaPlayer = MediaPlayer.create(this, R.raw.chopin) // initialize it here
                mMediaPlayer?.apply {
                    setOnPreparedListener(this@MediaPlayerService)
                    prepareAsync() // prepare async to not block main thread
                }

            }
        }
        return Service.START_STICKY
    }

    /** Called when MediaPlayer is ready */
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }
}
package com.example.rawan.radio

import android.app.Application
import com.example.rawan.radio.audioPlayer.PlaylistManager
import com.facebook.stetho.Stetho

/**
 * Created by rawan on 22/11/18.
 */
class MyApplication:Application(){
    val playlistManager: PlaylistManager by lazy { PlaylistManager(this) }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}
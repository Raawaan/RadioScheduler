package com.example.rawan.radio

import android.app.Application
import com.example.rawan.radio.audioPlayer.PlaylistManager
import com.facebook.stetho.Stetho
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger.executionWindow
import com.firebase.jobdispatcher.Constraint
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.text.format.Time
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.Trigger
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.HOURS
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.math.abs


/**
 * Created by rawan on 22/11/18.
 */
class MyApplication:Application(){
    val playlistManager: PlaylistManager by lazy { PlaylistManager(this) }
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(){
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Stetho.initializeWithDefaults(this)
    }
}


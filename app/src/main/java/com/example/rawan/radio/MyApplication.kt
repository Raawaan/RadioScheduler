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
import kotlin.math.abs


/**
 * Created by rawan on 22/11/18.
 */
class MyApplication:Application(){
    private val time = Time()

    val playlistManager: PlaylistManager by lazy { PlaylistManager(this) }

    override fun onCreate(){
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        time.setToNow()
        val now = time.hour*60000*60+time.minute*60000-6000
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val job = dispatcher.newJobBuilder()
                .setService(MyJobService::class.java)
                .setTag("my-recurring-tag")
                .setTrigger(Trigger.executionWindow(abs((86442000).minus(now)), abs((86454000).minus(now))))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .build()
        dispatcher.schedule(job)
    }
}


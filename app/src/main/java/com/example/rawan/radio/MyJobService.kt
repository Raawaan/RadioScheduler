package com.example.rawan.radio

import android.os.PersistableBundle
import android.text.format.Time
import android.widget.Toast
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.main.view.MainView
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import com.firebase.jobdispatcher.*
import java.util.*
import kotlin.math.abs

class MyJobService : JobService(), MainView {
    private val time = Time()
    lateinit var mainPresenter: MainPresenter


    override fun onStopJob(job: JobParameters?): Boolean {
        return false

    }

    override fun onStartJob(job: JobParameters?): Boolean {
        time.setToNow()
        val c = Calendar.getInstance()
        mainPresenter= MainPresenter(this, MainModel(RadioDatabase.getInstance(this)))
        mainPresenter.selectNextRadio((time.hour*60000*60+time.minute*60000-6000).toLong(),c.get(Calendar.DAY_OF_WEEK))
        NotificationUtilities.notification(this)
        return false
    }

    override fun nextRadio(nextRadio: RadioProgramEntity) {
        val bundle = PersistableBundle()
        bundle.putInt("radioId", nextRadio.radioId)
        bundle.putLong("stopService",nextRadio.toHour)
        startRadioPlayService(bundle, nextRadio)

    }
    private fun startRadioPlayService(bundle: PersistableBundle, nextRadio: RadioProgramEntity) {
        MyJobScheduler.radioJobScheduler(TimeInMilliSeconds.timeInMilli(nextRadio.fromHour),
                applicationContext,StartService::class.java,bundle,1 )
    }

    override fun toast(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        time.setToNow()
        val now = time.hour*60*60+time.minute*60
        val start =abs((86410).minus(now))
        val end =abs((86410).minus(now))+60
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val job = dispatcher.newJobBuilder()
                .setService(MyJobService::class.java)
                .setTag("my-recurring-tag")
                .setTrigger(Trigger.executionWindow(start, end))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setReplaceCurrent(true)
                .build()
        dispatcher.mustSchedule(job)
    }
}

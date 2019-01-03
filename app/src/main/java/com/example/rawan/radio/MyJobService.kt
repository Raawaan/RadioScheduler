package com.example.rawan.radio

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.text.format.Time
import android.widget.Toast
import com.example.rawan.radio.audioPlayer.MediaService
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.main.view.MainView
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import java.util.*
import kotlin.math.abs

class MyJobService : JobService(), MainView {

    private val time = Time()
    lateinit var mainPresenter: MainPresenter
    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartJob(p0: JobParameters?): Boolean {
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
        bundle.putLong("stopService", abs(nextRadio.toHour
                .minus((time.hour * 60000 * 60).plus(time.minute * 60000))))
        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if(!MediaService().isInstanceCreated())
        startRadioPlayService(bundle, nextRadio, jobScheduler)

    }
    private fun startRadioPlayService(bundle: PersistableBundle, nextRadio: RadioProgramEntity, jobScheduler: JobScheduler) {
        val componentName = ComponentName(applicationContext, StartService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
                .setExtras(bundle)
                .setMinimumLatency(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))))
                .setOverrideDeadline(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))))
                .build()
        jobScheduler.schedule(jobInfo)
    }

    override fun toast(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }
}

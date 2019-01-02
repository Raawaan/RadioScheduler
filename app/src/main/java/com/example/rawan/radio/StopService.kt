package com.example.rawan.radio

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.app.job.JobInfo
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.text.format.Time
import android.widget.Toast
import com.example.rawan.radio.audioPlayer.MediaService
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.main.view.MainView
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import kotlin.math.abs
import com.example.rawan.radio.main.view.MainActivity
import java.util.*


class StopService : JobService(), MainView {
    private val time = Time()
    lateinit var mainPresenter: MainPresenter
    override fun onStartJob(p0: JobParameters?): Boolean {
        time.setToNow()
        val c = Calendar.getInstance()
        Toast.makeText(this,"Stream Stopped",Toast.LENGTH_LONG).show()
        mainPresenter= MainPresenter(this, MainModel(RadioDatabase.getInstance(this)))
        mainPresenter.selectNextRadio((time.hour*60000*60+time.minute*60000-6000).toLong(),c.get(Calendar.DAY_OF_WEEK))
        val mediaService = Intent(this,  MediaService::class.java)
        stopService(mediaService)
        val startService = Intent(this,  StartService::class.java)
        stopService(startService)
        stopSelf()
        return  false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {

        return  false
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"Stop service Stopped",Toast.LENGTH_LONG).show()

    }
    override fun nextRadio(nextRadio: RadioProgramEntity) {
        val bundle = PersistableBundle()
        bundle.putInt("radioId", nextRadio.radioId)
        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(applicationContext, StartService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
                .setExtras(bundle)
                .setMinimumLatency(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))))
                .setOverrideDeadline(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))) +600)
                .build()
        jobScheduler.schedule(jobInfo)

        val componentName1 = ComponentName(applicationContext, StopService::class.java)
        val jobInfo1 = JobInfo.Builder(2, componentName1)
                .setMinimumLatency(abs(nextRadio.toHour
                        .minus((time.hour * 60000 * 60).plus(time.minute * 60000))))
                .setOverrideDeadline(abs(nextRadio.toHour
                        .minus((time.hour * 60000 * 60).plus(time.minute * 60000))) +600)
                .build()
        jobScheduler.schedule(jobInfo1)

    }

    override fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}

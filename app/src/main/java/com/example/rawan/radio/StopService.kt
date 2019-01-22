package com.example.rawan.radio

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.PersistableBundle
import android.text.format.Time
import android.widget.Toast
import com.example.rawan.radio.audioPlayer.MediaService
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.main.view.MainView
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import java.util.*


class StopService : JobService(), MainView {
    private val time = Time()
    private lateinit var mainPresenter: MainPresenter
    override fun onStartJob(p0: JobParameters?): Boolean {
        time.setToNow()
        val c = Calendar.getInstance()
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
    }
    override fun nextRadio(nextRadio: RadioProgramEntity) {
        val bundle = PersistableBundle()
        bundle.putInt("radioId", nextRadio.radioId)
        bundle.putLong("stopService",nextRadio.toHour)
        startRadioPlayService(bundle, nextRadio)
        sendBroadcast( WidgetNotifier.notify(application,this))
    }
    private fun startRadioPlayService(bundle: PersistableBundle, nextRadio: RadioProgramEntity) {
        MyJobScheduler.radioJobScheduler(TimeInMilliSeconds.timeInMilli(nextRadio.fromHour),
                applicationContext,StartService::class.java,bundle,1 )
    }
    override fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        sendBroadcast( WidgetNotifier.notify(application,this))
    }
}

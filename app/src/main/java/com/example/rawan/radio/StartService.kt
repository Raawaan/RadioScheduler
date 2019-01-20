package com.example.rawan.radio

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.example.rawan.radio.audioPlayer.MediaService
import android.app.ActivityManager
import android.support.v4.content.ContextCompat
import android.media.AudioManager




class StartService: JobService() {

    companion object {
            const val EXTRA_INDEX = "EXTRA_INDEX"
            var radioid = 0
            const val PLAYLIST_ID = 4 //Arbitrary, for the example
            fun radioId():Int{
                return radioid
            }
        }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startMediaService() {
        if (!isMyServiceRunning(MediaService::class.java)) {
            val intent = Intent(this, MediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, intent)
            } else {
                startService(intent)
            }
        }
    }

//    private val selectedPosition by lazy { intent.extras?.getInt(EXTRA_INDEX, 0) ?: 0 }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"start service Stopped",Toast.LENGTH_LONG).show()

    }
        override fun onStopJob(p0: JobParameters?): Boolean {

            return false
        }
    private fun stopRadioService(stopMediaTime: Long) {
        MyJobScheduler.radioJobScheduler(TimeInMilliSeconds.timeInMilli(stopMediaTime),
                applicationContext,StopService::class.java, null,3 )
    }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onStartJob(p0: JobParameters?): Boolean {
           p0?.extras?.get("stopService")?.toString()?.toLong()?.let { stopRadioService(it) }

            val wifiOnly= MySharedPreference.sharedPreference(this).getBoolean("internet",true)
            val internetState =InternetConnection.isOnWIFI(this)
            radioid=p0?.extras?.get("radioId")?.toString()?.toInt()!!
            if(InternetConnection.isOnline(this)){
                if (wifiOnly){
                    if(internetState){
                        startMediaService()
                    }
                    else
                        Toast.makeText(this,"please open your wifi",Toast.LENGTH_LONG).show()

                }else{
                    startMediaService()
                }
            }
            else
                Toast.makeText(this,"Check your internet connection",Toast.LENGTH_LONG).show()


            return false
        }

    }
package com.example.rawan.radio

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle

object MyJobScheduler {
    private lateinit var componentName :ComponentName
    private lateinit var jobInfo:JobInfo.Builder
    private lateinit var jobScheduler:JobScheduler
    fun radioJobScheduler(from:Long,applicationContext:Context,
                          serviceClass: Class<out Service>,bundleData: PersistableBundle ?,jobId:Int){
        jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        componentName = ComponentName(applicationContext,serviceClass)
        jobInfo = JobInfo.Builder(jobId, componentName)
                .setMinimumLatency(from)
                .setOverrideDeadline(from+6000)
        if (bundleData!=null){
            jobInfo.setExtras(bundleData)
                }
        jobScheduler.schedule(jobInfo.build())
    }

}
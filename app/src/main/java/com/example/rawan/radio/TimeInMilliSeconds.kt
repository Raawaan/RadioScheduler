package com.example.rawan.radio

import android.text.format.Time
import kotlin.math.abs

object TimeInMilliSeconds {
    private val time = Time()
    fun timeInMilli(fromHour:Long):Long{
        time.setToNow()
        return abs((fromHour.minus((time.hour * 60000 * 60)
                .plus(time.minute * 60000))))
    }
}
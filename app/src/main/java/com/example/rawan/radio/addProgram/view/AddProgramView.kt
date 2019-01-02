package com.example.rawan.radio.addProgram.view

import android.support.annotation.StringRes
import com.example.rawan.radio.addProgram.model.FromToDays

/**
 * Created by rawan on 25/11/18.
 */
interface AddProgramView {
    fun toast(@StringRes message:Int)
    fun toastString(message:String)
    fun intervalsOfSpecificDay(fromToDays:List<FromToDays>)
    fun createProgram()
}
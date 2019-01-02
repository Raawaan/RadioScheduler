package com.example.rawan.radio.main.view

import android.support.annotation.StringRes
import com.example.rawan.radio.radioDatabase.RadioProgramEntity

interface MainView {
    fun toast( message:String)
    fun nextRadio(nextRadio:RadioProgramEntity)

}
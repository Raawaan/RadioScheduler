package com.example.rawan.radio.editProgram.view

import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.radioDatabase.RadioProgramEntity

/**
 * Created by rawan on 03/12/18.
 */
interface EditProgramView {
    fun selectedDays(listOfSelectedDays:List<Int>)
    fun toast(msg:String)
    fun favorite(favorite:Int)

}
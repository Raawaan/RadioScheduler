package com.example.rawan.radio.listOfProgramRadios.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.rawan.radio.radioDatabase.RadioDatabase

/**
 * Created by rawan on 30/11/18.
 */
class ListOfProgramRadioViewModel(application: Application): AndroidViewModel(application) {

    val radioDatabase= RadioDatabase.getInstance(application)

    fun selectListOfProgramRadio(programName:String):LiveData<List<ListOfProgramRadio>> {
      return  radioDatabase.programDao().selectAllProgramsRadio(programName)
    }

}
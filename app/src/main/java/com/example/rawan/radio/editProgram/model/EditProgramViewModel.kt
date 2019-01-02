package com.example.rawan.radio.editProgram.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadio
import com.example.rawan.radio.radioDatabase.RadioDatabase

/**
 * Created by rawan on 03/12/18.
 */
class EditProgramViewModel(application: Application): AndroidViewModel(application){

    val radioDatabase= RadioDatabase.getInstance(application)

    fun selectListOfProgramRadio(programName:String): LiveData<List<ListOfProgramRadio>> {
        return  radioDatabase.programDao().selectAllProgramsRadio(programName)
    }

}
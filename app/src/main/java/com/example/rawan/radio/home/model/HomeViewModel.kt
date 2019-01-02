package com.example.rawan.radio.home.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.rawan.radio.radioDatabase.RadioDatabase
import android.arch.lifecycle.LiveData
import com.example.rawan.radio.radioDatabase.ProgramEntity


/**
 * Created by rawan on 29/11/18.
 */
class HomeViewModel(application: Application):AndroidViewModel(application) {

    val radioDatabase= RadioDatabase.getInstance(application)
    fun selectAllPrograms():LiveData<List<ProgramEntity>>{
        return radioDatabase.programDao().selectAll()
    }
    fun selectProgramAndRadioProgramByName():LiveData<List<ProgramAndRadioProgram>>{
        return radioDatabase.programDao().selectAllFromProgramAndRadioProgramOrderByProgramName()
    }
    fun selectProgramAndRadioProgramByFav():LiveData<List<ProgramAndRadioProgram>>{
        return radioDatabase.programDao().selectAllFromProgramAndRadioProgramOrderByFavorite()
    }

}
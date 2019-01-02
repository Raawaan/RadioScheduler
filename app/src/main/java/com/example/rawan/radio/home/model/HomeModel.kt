package com.example.rawan.radio.home.model

import com.example.rawan.radio.radioDatabase.RadioDatabase
import io.reactivex.Observable

/**
 * Created by rawan on 28/11/18.
 */
class HomeModel(private val database: RadioDatabase) {
    fun deleteProgram(programName:String):Observable<Int> {
       return Observable.fromCallable {
        val programEntity = database.programDao().selectAllWhereName(programName)
        val radioProgramEntity = database.radioProgramDao().selectWithProgramId(programEntity.programId)
        database.radioProgramDao().deleteProgramFromRadioProgram(radioProgramEntity)
        val listOfProgramDays= database.programDaysDao().selectAllProgramDays(programEntity.programId)
        database.programDaysDao().deleteAll(listOfProgramDays)
        database.programDao().deleteProgram(programEntity)
        }
    }
}
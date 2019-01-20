package com.example.rawan.radio.listOfProgramRadios.model

import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.radioDatabase.RadioDatabase
import io.reactivex.Observable

/**
 * Created by rawan on 30/11/18.
 */
class ListOfProgramsRadiosModel(private val database: RadioDatabase){
    fun countWithProgramId(programId:Int):Observable<Int>{
        return Observable.fromCallable {
                 database.radioProgramDao().countWithProgramId(programId)
             }
        }
    fun deleteRadioProgram(radioId:Int,programId:Int,fromHour:Long):Observable<Unit>{
       return Observable.fromCallable {
           val radioProgramEntity= database.radioProgramDao().selectAllWhereRadioIdAndProgramId(radioId,programId,fromHour)
             database.radioProgramDao().delete(radioProgramEntity)
       }
    }
    fun updateRadio(fromHour:Long, toHour:Long, programId:Int, radioId:Int, fromHourExisted:Long):Observable<Unit>{
        return Observable.fromCallable {
            database.radioProgramDao().updateRadio(fromHour,toHour,programId,radioId,fromHourExisted)
        }
    }
    fun selectFromToDay(programName:String):Observable<List<FromToDays>>{
        return Observable.fromCallable {
            database.programDaysDao().selectHoursFromToAndDaysWhereName(programName)
        }
    }
    fun deleteProgram(programId: Int):Observable<Int>{
        return Observable.fromCallable {
            val programEntity = database.programDao().selectAllWhereId(programId)
            val listOfProgramDays= database.programDaysDao().selectAllProgramDays(programEntity.programId)
            database.programDaysDao().deleteAll(listOfProgramDays)
            database.programDao().deleteProgram(programEntity)
        }
    }
}
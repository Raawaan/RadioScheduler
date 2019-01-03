package com.example.rawan.radio.radioDatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.rawan.radio.home.model.ProgramAndRadioProgram

/**
 * Created by rawan on 27/11/18.
 */
@Dao
interface RadioProgramDao{
    @Insert
    fun insertRadioProgram(radioProgramEntity: List<RadioProgramEntity>)

    @Query("select * from radioProgram where programId=:programId")
    fun selectWithProgramId(programId:Int):List<RadioProgramEntity>

    @Query("select * from radioProgram,program where programName=:programName")
    fun selectWithProgramName(programName:String):List<RadioProgramEntity>

    @Delete
    fun deleteProgramFromRadioProgram(listOfRadioProgram:List<RadioProgramEntity>)

    @Query("select * from radioProgram where radioId=:radioId and programId=:programId and fromHour=:fromHour")
    fun selectAllWhereRadioIdAndProgramId(radioId:Int,programId:Int,fromHour:Long):RadioProgramEntity

    @Query("update radioProgram set fromHour =:fromHour, toHour=:toHour where programId=:programId AND radioId=:radioId AND fromHour=:fromHourExisted ")
    fun updateRadio(fromHour:Long, toHour:Long, programId:Int, radioId:Int,fromHourExisted:Long)

 @Query("select * from radioProgram,programDay where fromHour>:currentTime AND programDay.days=:day AND radioProgram.programId=programDay.programId ORDER BY fromHour LIMIT 1")
    fun selectNextRadio(currentTime:Long,day:Int):RadioProgramEntity
    @Delete
    fun delete(radioProgramEntity: RadioProgramEntity)
}
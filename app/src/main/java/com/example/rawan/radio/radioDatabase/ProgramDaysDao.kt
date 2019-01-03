package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.rawan.radio.addProgram.model.FromToDays

/**
 * Created by rawan on 25/11/18.
 */
@Dao
interface ProgramDaysDao{

    @Insert
    fun insertDays(programDayEntity: ProgramDayEntity)

    @Query("select * from programDay where programId=:programId")
    fun selectAllProgramDays(programId:Int):List<ProgramDayEntity>

    @Delete
    fun deleteAll(listOfProgramDays :List<ProgramDayEntity>)

    @Delete
    fun deleteDay(listOfProgramDays :ProgramDayEntity)

    @Query("select ProgramDayId from programDay where programId=:programId AND days=:days" )
    fun checkIfDaysSelected(programId:Int,days:Int):Long

    @Query("DELETE FROM programDay WHERE programId=:programId AND days=:days")
    fun disable(programId:Int,days:Int)

    @Query("select fromHour,toHour,days from radioProgram,programDay where radioProgram.programId=programDay.programId and days in(:day)")
    fun selectHoursFromToAndDays(day:List<Int>):List<FromToDays>

    @Query("select fromHour,toHour,days from radioProgram,programDay where radioProgram.programId=programDay.programId and days in(" +
            " select days from program,programDay where program.programName =:programName AND program.programId=programDay.programId)")
    fun selectHoursFromToAndDaysWhereName(programName:String):List<FromToDays>

    @Query("select days from programDay,program where " +
            "program.programId=programDay.programId and programName=:programName")
    fun selectDaysOfProgramName(programName:String):List<Int>

}
package com.example.rawan.radio.radioDatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.rawan.radio.home.model.ProgramAndRadioProgram
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadio


/**
 * Created by rawan on 22/11/18.
 */
@Dao
interface ProgramDao{
    @Insert
    fun insertAll(programEntity: ProgramEntity):Long

    @Query("select programId from program where programName=:programName" )
    fun selectIdWhereName(programName:String):Int

    @Query("select * from program where programName=:programName" )
    fun selectAllWhereName(programName:String):ProgramEntity

    @Query("select * from program where programId=:programId" )
    fun selectAllWhereId(programId:Int):ProgramEntity

    @Query("SELECT * from program")
    fun selectAll():LiveData<List<ProgramEntity>>

    @Query("select favorite fROM program where programName=:programName")
    fun selectFavorite(programName:String):Int

    @Query("update program set favorite =:favorite whERE programName=:programName")
    fun updateFavorite(favorite:Int,programName:String)

    @Query("update program set programImage =:ImagePath whERE programName=:programName")
    fun updateProgramImage(ImagePath:String,programName:String)

    @Delete
    fun deleteProgram(programEntity: ProgramEntity):Int

    @Query("select * from program,radioProgram,radio where program.programId=radioProgram.programId AND " +
            "program.programName=:programName AND radio.radioId=radioProgram.radioId order by radioProgram.fromHour ")
    fun selectAllProgramsRadio(programName:String):LiveData<List<ListOfProgramRadio>>

     @Query("select program.programId, programName,programImage,favorite,min(fromHour) as 'from' , max(toHour) as 'to' from radioProgram ," +
            " program where program.programId=radioProgram.programId group by program.programId order by program.programName")
    fun selectAllFromProgramAndRadioProgramOrderByProgramName():LiveData<List<ProgramAndRadioProgram>>

    @Query("select program.programId, programName,programImage,favorite,min(fromHour) as 'from' , max(toHour) as 'to' from radioProgram ," +
            " program where program.programId=radioProgram.programId group by program.programId order by program.favorite DESC")
    fun selectAllFromProgramAndRadioProgramOrderByFavorite():LiveData<List<ProgramAndRadioProgram>>


}


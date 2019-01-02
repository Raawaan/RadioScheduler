package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rawan on 22/11/18.
 */
@Entity(tableName = "radioProgram",
        foreignKeys =
            [(ForeignKey(entity = RadioEntity::class,
                parentColumns = arrayOf("radioId"),
                childColumns = arrayOf("radioId"))),
                (ForeignKey(entity = ProgramEntity::class,
                    parentColumns = arrayOf("programId"),
                    childColumns = arrayOf("programId")))
            ]
)
data class RadioProgramEntity(@PrimaryKey(autoGenerate = true)val radioProgramId:Int,
                              val radioId:Int,
                              val programId:Int,
                              val fromHour:Long,
                              val toHour :Long){
    @Ignore
constructor(radioId: Int,
              programId:Int,
              fromHour:Long,
              toHour :Long):this(0,radioId,programId,fromHour,toHour)
}
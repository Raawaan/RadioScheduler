package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rawan on 25/11/18.
 */
@Entity(tableName = "programDay",
        foreignKeys =
        [(ForeignKey(entity = ProgramEntity::class,
                parentColumns = arrayOf("programId"),
                childColumns = arrayOf("programId")))
        ]
)
data class ProgramDayEntity(@PrimaryKey(autoGenerate = true)val ProgramDayId:Int,
                              val programId:Int,
                              val days:Int){
        @Ignore
        constructor(programId: Int,days: Int): this(0,programId,days)
}
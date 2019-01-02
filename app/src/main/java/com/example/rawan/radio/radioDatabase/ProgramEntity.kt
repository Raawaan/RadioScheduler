package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rawan on 22/11/18.
 */
@Entity(tableName = "program")
data class ProgramEntity(@PrimaryKey(autoGenerate = true) val programId:Int,
                         val programName:String,
                         val programImage:String,
                         val favorite :Int) {
    @Ignore
    constructor(programName: String,programImage: String,favorite: Int): this(0,programName,programImage,favorite)
}
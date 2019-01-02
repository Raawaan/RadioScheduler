package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rawan on 22/11/18.
 */
@Entity(tableName = "radio")
data class RadioEntity(@PrimaryKey(autoGenerate = true) val radioId:Int,
                       val radioName:String,
                       val radioImage:String){
    @Ignore
    constructor(radioName: String,radioImage: String): this(0,radioName,radioImage)
}
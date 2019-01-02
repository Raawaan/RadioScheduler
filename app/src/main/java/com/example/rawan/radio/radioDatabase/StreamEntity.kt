package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rawan on 22/11/18.
 */
@Entity(tableName = "stream",
        foreignKeys = [(ForeignKey(entity = RadioEntity::class,
                parentColumns = arrayOf("radioId"),
                childColumns = arrayOf("radioId")))
        ])
data class StreamEntity(@PrimaryKey(autoGenerate = true) val streamId:Int,
                        val radioId:Int,
                        val stream:String){
        @Ignore
        constructor(radioId: Int,stream: String):this(0,radioId,stream)
}
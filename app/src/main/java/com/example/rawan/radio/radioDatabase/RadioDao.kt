package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by rawan on 27/11/18.
 */
@Dao
interface RadioDao {
        @Insert
        fun insertAll(listOfRadios:RadioEntity):Long

        @Query("select radioId from radio where radioName=:radioName")
        fun selectRadioIdByName(radioName:String):Int
}
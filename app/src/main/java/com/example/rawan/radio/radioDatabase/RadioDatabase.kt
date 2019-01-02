package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by rawan on 22/11/18.
 */
@Database(entities = [RadioEntity::class,StreamEntity::class,
ProgramEntity::class,RadioProgramEntity::class,ProgramDayEntity::class], version =1)
abstract class RadioDatabase : RoomDatabase() {
        abstract fun programDao():ProgramDao
        abstract fun radioDao():RadioDao
        abstract fun radioProgramDao():RadioProgramDao
        abstract fun programDaysDao():ProgramDaysDao
        abstract fun steamDao():StreamDao
    companion object {
        var DATABASE_NAME = "RadioDatabase"
        private var INSTANCE: RadioDatabase? = null
        @Synchronized
        fun getInstance(context: Context): RadioDatabase {
            if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.applicationContext,
                                    RadioDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                }
            return INSTANCE!!
        }
    }
}
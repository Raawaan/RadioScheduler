package com.example.rawan.radio.main.model

import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import io.reactivex.Observable
import java.util.*

class MainModel(private val database: RadioDatabase){
    fun selectNextRadio(currentTime:Long,day:Int):Observable<RadioProgramEntity> {
       return Observable.fromCallable{
           database.radioProgramDao().selectNextRadio(currentTime,day)
        }
    }
}
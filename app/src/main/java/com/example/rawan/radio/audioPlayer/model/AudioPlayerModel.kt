package com.example.rawan.radio.audioPlayer.model

import com.example.rawan.radio.radioDatabase.RadioDatabase
import io.reactivex.Observable

class AudioPlayerModel(val database: RadioDatabase){
    fun selectStreams(radioId:Int):Observable<List<ListOfStreams>>{
        return Observable.fromCallable {
            database.steamDao().selectAllStreams(radioId)
        }
    }
}
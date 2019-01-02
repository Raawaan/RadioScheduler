package com.example.rawan.radio.radioDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.rawan.radio.audioPlayer.model.ListOfStreams

/**
 * Created by rawan on 28/11/18.
 */
@Dao
interface StreamDao{
    @Insert
    fun insertStream(streamEntity: StreamEntity)

    @Query("select stream,radioImage,radioName from " +
            "stream,radio where radio.radioId= stream.radioId AND radio.radioId=:radioId")
    fun selectAllStreams(radioId:Int):List<ListOfStreams>
}
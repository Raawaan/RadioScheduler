package com.example.rawan.radio.audioPlayer.view

import com.example.rawan.radio.audioPlayer.model.ListOfStreams

interface AudioPlayerUI {
    fun listOfStreams(listOfStreams :List<ListOfStreams>)
    fun toast(message:String)
}
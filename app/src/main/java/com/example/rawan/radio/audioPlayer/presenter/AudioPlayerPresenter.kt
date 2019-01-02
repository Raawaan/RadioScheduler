package com.example.rawan.radio.audioPlayer.presenter

import android.os.Build
import android.support.annotation.RequiresApi
import com.example.rawan.radio.audioPlayer.model.AudioPlayerModel
import com.example.rawan.radio.audioPlayer.view.AudioPlayerUI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class AudioPlayerPresenter(val audioPlayerUi:AudioPlayerUI,val audioPlayerModel:AudioPlayerModel) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun selectStream(radioId:Int) : Boolean{
        audioPlayerModel.selectStreams(radioId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            audioPlayerUi.listOfStreams(it)
                        },
                        onError = {
                            audioPlayerUi.toast(it.toString())

                        })
        return true

    }
}
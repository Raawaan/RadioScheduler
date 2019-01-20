package com.example.rawan.radio.main.presenter

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.example.rawan.radio.MyAppWidgetProvider
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MainPresenter(private val mainView: MainView,private val mainModel: MainModel){
    fun selectNextRadio(currentTime:Long,day:Int){

        mainModel.selectNextRadio(currentTime,day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            mainView.nextRadio(it)
                        },
                        onError = {
                          mainView.toast("No Upcoming Radios To Play")
                        })
    }
}
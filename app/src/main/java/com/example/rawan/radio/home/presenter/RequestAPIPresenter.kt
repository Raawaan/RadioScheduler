package com.example.rawan.radio.home.presenter

import com.example.rawan.radio.home.model.HomeModel
import com.example.rawan.radio.home.view.HomeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by rawan on 13/11/18.
 */
class RequestAPIPresenter(private val homeModel: HomeModel, val homeView: HomeView) {
    fun deleteProgram(programName:String){
        homeModel.deleteProgram(programName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            homeView.toast("Program deleted")
                        },
                        onError = {
                            homeView.toast(it.message.toString())
                        })
    }
}
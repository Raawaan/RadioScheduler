package com.example.rawan.radio.searchForRadio.presenter

import android.os.Build
import android.support.annotation.RequiresApi
import com.example.rawan.radio.requestRadio.StationsByCountry
import com.example.rawan.radio.searchForRadio.model.SearchForRadioModel
import com.example.rawan.radio.searchForRadio.view.SearchForRadioView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by rawan on 25/11/18.
 */
class SearchForRadioPresenter(private val searchForRadioModel: SearchForRadioModel ,private val searchForRadioView: SearchForRadioView) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun requestRadios(countryCode:String){
        StationsByCountry.connectToAPI(countryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                        onNext = {
                            searchForRadioView.listOfStations(it)
                            if(it.isEmpty()){
                                searchForRadioView.errorMsg("No Radio available for this country")
                            }
                        },
                        onError = {
                            searchForRadioView.errorMsg(it.toString())
                        })
    }
}
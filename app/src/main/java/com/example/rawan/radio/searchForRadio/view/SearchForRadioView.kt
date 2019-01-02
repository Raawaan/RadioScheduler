package com.example.rawan.radio.searchForRadio.view

import com.example.rawan.radio.requestRadio.StationInfo

/**
 * Created by rawan on 25/11/18.
 */
interface SearchForRadioView {
    fun listOfStations(listOfStations:List<StationInfo>)
    fun errorMsg(errorMsg:String)
}
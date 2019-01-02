package com.example.rawan.radio.requestRadio

import android.os.Build
import android.support.annotation.RequiresApi
import io.reactivex.Observable

/**
 * Created by rawan on 13/11/18.
 */
object StationsByCountry {
    @RequiresApi(Build.VERSION_CODES.N)
    fun connectToAPI(countryName: String): Observable<List<StationInfo>> {
        return StationAPI.create().getData(countryName).flatMapIterable { list -> list }
                .map {
                    return@map StationInfo(
                            it.name,
                            it.streams!!.map {
                                it.stream
                            },
                            it.image?.url,
                            it.categories?.get(0)?.description,
                            it.facebook)
                }.toList().toObservable()

    }
}
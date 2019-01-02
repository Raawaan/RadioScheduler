package com.example.rawan.radio.requestRadio

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by rawan on 13/11/18.
 */
interface StationAPI{
    @GET("v2/countries/{countryName}/" +
            "stations?token=8d987eab640b989b4d94308174")
    fun getData(@Path("countryName") countryName:String):
            Observable<List<Response>>

    companion object {
       private val BASE_URL= "http://api.dirble.com/"
        fun create(): StationAPI {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(StationAPI::class.java)
        }
    }
}
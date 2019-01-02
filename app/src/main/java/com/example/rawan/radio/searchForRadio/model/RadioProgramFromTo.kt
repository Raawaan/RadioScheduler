package com.example.rawan.radio.searchForRadio.model

import android.arch.persistence.room.Ignore
import android.os.Parcelable
import com.example.rawan.radio.requestRadio.StreamsItem
import kotlinx.android.parcel.Parcelize

/**
 * Created by rawan on 25/11/18.
 */
@Parcelize
data class RadioProgramFromTo(val name:String?,
                              val image:String?,
                              val fromHour:Long,
                              val toHour:Long,
                              val stream: List<String>?,
                              val description: String?,
                              val facebookUrl: String?):Parcelable



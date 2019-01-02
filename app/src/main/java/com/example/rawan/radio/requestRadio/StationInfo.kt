package com.example.rawan.radio.requestRadio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by rawan on 13/11/18.
 */
@Parcelize
data class StationInfo(
        val name: String?,
        val stream: List<String>,
        val imgUrl: String?,
        val description: String?,
        val facebookUrl: String?
) : Parcelable
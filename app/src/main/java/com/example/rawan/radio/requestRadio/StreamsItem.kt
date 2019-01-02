package com.example.rawan.radio.requestRadio

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StreamsItem( var stream: String = ""):Parcelable
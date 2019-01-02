package com.example.rawan.radio.requestRadio

import com.google.gson.annotations.SerializedName

class Image {

    @SerializedName("url")
    var url: String? = null

    override fun toString(): String {
        return "Image{" +
                ",url = '" + url + '\''.toString() +
                "}"
    }
}
package com.example.rawan.radio.requestRadio

import com.google.gson.annotations.SerializedName

class Response {

    @SerializedName("image")
    var image: Image? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("streams")
    val streams: List<StreamsItem>? = null

    @SerializedName("facebook")
    var facebook: String? = null

    @SerializedName("categories")
    var categories: List<CategoriesItem>? = null


    override fun toString(): String {
        return "Response{" +
                ",image = '" + image + '\''.toString() +
                ",name = '" + name + '\''.toString() +
                ",streams = '" + streams + '\''.toString() +
                ",facebook = '" + facebook + '\''.toString() +
                ",categories = '" + categories + '\''.toString() +
                "}"
    }
}
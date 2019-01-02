package com.example.rawan.radio.requestRadio

/**
 * Created by rawan on 13/11/18.
 */
import com.google.gson.annotations.SerializedName

class CategoriesItem {

    @SerializedName("description")
    var description: String? = null

    override fun toString(): String {
        return "CategoriesItem{" +
                ",description = '" + description + '\''.toString() +
                "}"
    }
}
package com.example.rawan.radio

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object MySharedPreference {
    private lateinit var sharedPreferences:SharedPreferences
    fun editor():SharedPreferences.Editor{
        return sharedPreferences.edit()
    }
    fun sharedPreference(context: Context):SharedPreferences{
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences
    }

}
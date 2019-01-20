package com.example.rawan.radio

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE



object InternetConnection{
        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
       fun isOnWIFI(context: Context):Boolean{
           val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
           return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting
       }
}
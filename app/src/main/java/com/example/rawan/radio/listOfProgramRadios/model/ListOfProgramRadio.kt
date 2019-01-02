package com.example.rawan.radio.listOfProgramRadios.model

/**
 * Created by rawan on 30/11/18.
 */
data class ListOfProgramRadio(val radioName:String?,
                              val radioImage:String?,
                              val fromHour:Long,
                              val toHour:Long,
                              val programId :Int,
                              val radioId:Int)
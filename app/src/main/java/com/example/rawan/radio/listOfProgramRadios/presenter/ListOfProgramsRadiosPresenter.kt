package com.example.rawan.radio.listOfProgramRadios.presenter

import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramsRadiosModel
import com.example.rawan.radio.listOfProgramRadios.view.ListOfProgramsRadiosView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by rawan on 30/11/18.
 */
class ListOfProgramsRadiosPresenter(private val listOfProgramsRadiosModel: ListOfProgramsRadiosModel,
                                  private  val listOfProgramsRadiosView: ListOfProgramsRadiosView){
    fun deleteRadioProgram(radioId:Int,programId:Int,fromHour:Long){
        listOfProgramsRadiosModel.deleteRadioProgram(radioId, programId,fromHour)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            listOfProgramsRadiosView.toast("Radio deleted")
                        },
                        onError = {
//                            homeView.toast(it.message.toString())
                        })
    }
    private fun updateRadio(fromHour:Long, toHour:Long, programId:Int, radioId:Int, fromHourExisted:Long){
        listOfProgramsRadiosModel.updateRadio(fromHour,toHour,programId,radioId,fromHourExisted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            listOfProgramsRadiosView.toast("updated")
                        },
                        onError = {
                        listOfProgramsRadiosView.toast("radio was not updated")
                        })
    }
    fun selectFromToDay(programName:String,fromHour:Long, toHour:Long, programId:Int, radioId:Int, fromHourExisted:Long){
        listOfProgramsRadiosModel.selectFromToDay(programName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {fromToDaysList->
                            var flag = true
                            fromToDaysList.map {
                                if (!it.fromHour.equals(fromHourExisted)) {
                                    if((fromHour in it.fromHour..it.toHour) ||
                                        (toHour in it.fromHour..it.toHour) ||
                                        it.fromHour in fromHour..toHour ||
                                        it.toHour in fromHour..toHour){
                                    listOfProgramsRadiosView.toast("overlap with another program")
                                    flag=false
                                    }
                                }
                            }
                            if (flag){
                                listOfProgramsRadiosView.dismiss()
                                updateRadio(fromHour,toHour,programId,radioId,fromHourExisted)
                            }
                        },
                        onError = {
                            listOfProgramsRadiosView.toast("radio was not updated")
                        })
    }
}
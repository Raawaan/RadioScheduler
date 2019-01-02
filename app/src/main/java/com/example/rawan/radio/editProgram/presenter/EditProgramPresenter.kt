package com.example.rawan.radio.editProgram.presenter

import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.editProgram.model.EditProgramModel
import com.example.rawan.radio.editProgram.view.EditProgramView
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by rawan on 03/12/18.
 */
class EditProgramPresenter(private val editProgramModel: EditProgramModel
                           ,private val editProgramView: EditProgramView,private val programName: String) {
    fun selectSelectedDaysOfProgram(programName: String) {
        editProgramModel.selectSelectedDaysofProgram(programName).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    editProgramView.selectedDays(it)
                }, onError = {
                    editProgramView.toast(it.toString())
                })
    }

    fun selectFavorite(programName: String) {
    editProgramModel.selectFavorite(programName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                editProgramView.favorite(it)
            }, onError = {
                editProgramView.toast(it.toString())
            })

    }
    fun enableDays(dayNum:Int,programName: String){
        editProgramModel.enableDays(dayNum,programName).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                }, onError = {
                    editProgramView.toast(it.toString())
                })
    }
    fun disable(dayNum:Int,programName: String){
        editProgramModel.disableDay(dayNum,programName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                }, onError = {
                    editProgramView.toast(it.toString())
                })
    }
    fun updateFav(favorite:Int,programName:String){
        editProgramModel.updateFav(favorite, programName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                }, onError = {
                    editProgramView.toast(it.toString())
                })
    }
    fun selectFromToDay(days:List<Int>,programName:String,list: List<RadioProgramFromTo>,listOfExsitedAndChosenRadiosModel:MutableList<RadioProgramFromTo>?,selectedDays:List<Int>){
        editProgramModel.selectFromToDay(days)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { daysFromToAvailableInDatabase->
                            val listOfDaysInDatabase= mutableListOf<Int>()
                               daysFromToAvailableInDatabase.forEach {
                                   listOfDaysInDatabase.add(it.days)
                               }
                            days.map {
                                if (!listOfDaysInDatabase.contains(it)){
                                    enableDays(it,programName)
                                }
                            }
                            enableSelectedDays(daysFromToAvailableInDatabase,listOfExsitedAndChosenRadiosModel)
                        },
                        onError = {
                            editProgramView.toast(it.localizedMessage)
                        })
        editProgramModel.selectFromToDay(selectedDays)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { daysFromToAvailableInDatabase->
                          addRadiosIfThereIsNoOverlap(daysFromToAvailableInDatabase,programName,list)
                        },
                        onError = {
                            editProgramView.toast(it.localizedMessage)
                        })

    }
    private fun enableSelectedDays(fromToDays: List<FromToDays>, listOfExsitedAndChosenRadiosModel:MutableList<RadioProgramFromTo>?) {
        val setOfDaysToBeExcluded = mutableSetOf<Int>()
        listOfExsitedAndChosenRadiosModel?.map {
            fromToDays.forEach {objectsFromDatabase ->
                if ((objectsFromDatabase.fromHour in it.fromHour..it.toHour) ||
                        (objectsFromDatabase.toHour in it.fromHour..it.toHour)
                        || it.fromHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour||
                        it.toHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour ) {
                    editProgramView.toast("${objectsFromDatabase.days} was not added")
                   setOfDaysToBeExcluded.add(objectsFromDatabase.days)
                }
            }
        }
        fromToDays.map {
            if (!setOfDaysToBeExcluded.contains(it.days)){
                enableDays(it.days, programName)
            }
        }
    }
    private fun addRadiosIfThereIsNoOverlap(fromToDays: List<FromToDays>,programName:String,list: List<RadioProgramFromTo>){
      val listOfRadiosThatCanBeAdded= mutableSetOf<RadioProgramFromTo>()
        list.map {
            var flag = true
            fromToDays.forEach {objectsFromDatabase ->
                if ((objectsFromDatabase.fromHour in it.fromHour..it.toHour) ||
                        (objectsFromDatabase.toHour in it.fromHour..it.toHour)
                        || it.fromHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour||
                        it.toHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour ) {
                    editProgramView.toast("radio at ${TimeUnit.MILLISECONDS.toMinutes(objectsFromDatabase.fromHour).div(60)} was not added")
                    flag=false
                    return@forEach
                }
            }
            if (flag){
            listOfRadiosThatCanBeAdded.add(it)
            }
        }
        addRadio(programName,listOfRadiosThatCanBeAdded.toList())

    }
    private fun addRadio(programName:String,list: List<RadioProgramFromTo>){
        editProgramModel.addRadio(programName,list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {

                }, onError = {
                    editProgramView.toast(it.toString())
                })
    }
}

package com.example.rawan.radio.addProgram.presenter

import com.example.rawan.radio.R
import com.example.rawan.radio.addProgram.model.AddProgramModel
import com.example.rawan.radio.addProgram.model.RadioIsExistedException
import com.example.rawan.radio.addProgram.view.AddProgramView
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by rawan on 25/11/18.
 */
class AddProgramPresenter(private val addProgramModel: AddProgramModel,private val addProgramView: AddProgramView){

    fun addProgram(name:String,image:String,favorite:Int,
                   selectedDays:List<Int>,list: List<RadioProgramFromTo>){
        addProgramModel.addPrograms(name, image, favorite, selectedDays, list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            addProgramView.toast(R.string.msg_program_created)
                            addProgramView.createProgram()
                        },
                        onError = {
                            if (it is RadioIsExistedException){
                                addProgramView.toast(R.string.warning_program_existed)
                            }
                            else
                            addProgramView.toast(R.string.error_add_program)
                        })

    }
    fun selectFromToDay(day:List<Int>){
        addProgramModel.selectFromToDay(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            addProgramView.intervalsOfSpecificDay(it)
                        },
                        onError = {
                            addProgramView.toastString(it.localizedMessage)

                        })
    }
}
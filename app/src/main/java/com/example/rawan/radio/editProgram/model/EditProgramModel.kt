package com.example.rawan.radio.editProgram.model

import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.radioDatabase.*
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import io.reactivex.Observable

/**
 * Created by rawan on 03/12/18.
 */
class EditProgramModel(val radioDatabase: RadioDatabase) {
    private var radio:RadioEntity=RadioEntity(0,"","")
    private val listOfRadioProgram:MutableList<RadioProgramEntity> = mutableListOf()

    fun selectFromToDay(day:List<Int>):Observable<List<FromToDays>>{
        return Observable.fromCallable {
            radioDatabase.programDaysDao().selectHoursFromToAndDays(day)
        }
    }
    fun selectSelectedDaysofProgram(programName: String): Observable<List<Int>> {
        return Observable.fromCallable {
            radioDatabase.programDaysDao().selectDaysOfProgramName(programName)
        }
    }

    fun selectFavorite(programName: String): Observable<Int> {
        return Observable.fromCallable {
            radioDatabase.programDao().selectFavorite(programName)
        }
    }

    fun enableDays(dayNum: Int, programName: String): Observable<Unit> {
        return Observable.fromCallable {
            radioDatabase.programDaysDao().insertDays(ProgramDayEntity(
                    radioDatabase.programDao().selectIdWhereName(programName)
                    , dayNum
            ))
        }
    }

    fun disableDay(dayNum: Int, programName: String): Observable<Unit> {
        return Observable.fromCallable {
                  radioDatabase.programDaysDao().disable(
                    radioDatabase.programDao().selectIdWhereName(programName)
                    , dayNum
            )
        }
    }
    fun updateFav(favorite:Int,programName:String): Observable<Unit> {
        return Observable.fromCallable {
            radioDatabase.programDao().updateFavorite(favorite,programName)
        }
    }
    private fun addRadio(listOfStreams: List<String>?,radio: RadioEntity):Int {
        val id = radioDatabase.radioDao().selectRadioIdByName(radio.radioName)
        return if(id!=0)
            id
        else{
            val idOfInsertedRadio=  radioDatabase.radioDao().insertAll(radio).toInt()
            addListOfStream(listOfStreams,idOfInsertedRadio)
            idOfInsertedRadio
        }
    }
    private fun addListOfStream(listOfStreams: List<String>?, radioId:Int){
        listOfStreams?.map {
            radioDatabase.steamDao().insertStream(StreamEntity(radioId,it))
        }
    }
    private fun insertRadioProgram(radioProgramEntity: List<RadioProgramEntity>){
        radioDatabase.radioProgramDao().insertRadioProgram(radioProgramEntity)
    }

    fun addRadio(programName:String,list: List<RadioProgramFromTo>):Observable<Unit>{
       return Observable.fromCallable {
           var idOfInsertedRadio: Int
            list.map {
                radio = RadioEntity(
                        it.name.toString(),
                        it.image.toString()
                )
                idOfInsertedRadio = addRadio(it.stream, radio)
                listOfRadioProgram.add(RadioProgramEntity(
                        idOfInsertedRadio,
                        radioDatabase.programDao().selectIdWhereName(programName),
                        it.fromHour,
                        it.toHour
                ))
            }
            insertRadioProgram(listOfRadioProgram)
        }
    }
}
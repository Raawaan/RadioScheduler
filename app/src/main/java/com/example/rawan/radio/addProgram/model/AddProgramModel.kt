package com.example.rawan.radio.addProgram.model

import com.example.rawan.radio.radioDatabase.*
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import io.reactivex.Observable

/**
 * Created by rawan on 25/11/18.
 */
class AddProgramModel(private val database: RadioDatabase){
    fun selectFromToDay(day:List<Int>):Observable<List<FromToDays>>{
       return Observable.fromCallable {
           database.programDaysDao().selectHoursFromToAndDays(day)
        }
    }
    private fun addProgramToDatabase(name:String,image:String,favorite:Int,selectedDays:List<Int>):Observable<Long>{
        // check if name already existed
        return Observable.fromCallable {
            if (database.programDao().selectIdWhereName(name) != 0)
                0
            else {
                val programId = database.programDao().insertAll(ProgramEntity(name, image, favorite))
                addSelectedDays(
                        programId.toInt()
                        , selectedDays)
                programId
            }
        }
    }
    private fun addSelectedDays(programId:Int,selectedDays:List<Int>){
        selectedDays.map {
            database.programDaysDao().insertDays(ProgramDayEntity(programId,it))
        }
    }
    private fun addRadio(listOfStreams: List<String>?,radio:RadioEntity):Int {
       val id = database.radioDao().selectRadioIdByName(radio.radioName)
        return if(id!=0)
            id
        else{
             val idOfInsertedRadio=  database.radioDao().insertAll(radio).toInt()
             addListOfStream(listOfStreams,idOfInsertedRadio)
             idOfInsertedRadio
        }
    }
    private fun insertRadioProgram(radioProgramEntity: List<RadioProgramEntity>){
        database.radioProgramDao().insertRadioProgram(radioProgramEntity)
    }
    private fun addListOfStream(listOfStreams: List<String>?, radioId:Int){
        listOfStreams?.map {
            database.steamDao().insertStream(StreamEntity(radioId,it))
        }
    }
    private var radio:RadioEntity=RadioEntity(0,"","")
    private val listOfRadioProgram:MutableList<RadioProgramEntity> = mutableListOf()
    fun addPrograms(name: String, image: String, favorite: Int,
                    selectedDays: List<Int>, list: List<RadioProgramFromTo>) :Observable<Unit> {
    return addProgramToDatabase(name
                 , image, favorite, selectedDays).map {
             var idOfInsertedRadio: Int
             val programId = it.toInt()
             if (programId == 0)
                throw RadioIsExistedException()
             else {
                 list.map {
                     radio = RadioEntity(
                             it.name.toString(),
                             it.image.toString()
                     )
                     idOfInsertedRadio = addRadio(it.stream, radio)
                     listOfRadioProgram.add(RadioProgramEntity(
                             idOfInsertedRadio,
                             programId,
                             it.fromHour,
                             it.toHour
                     ))
                 }

                 insertRadioProgram(listOfRadioProgram)

             }

        }
    }

}

class RadioIsExistedException : Exception()
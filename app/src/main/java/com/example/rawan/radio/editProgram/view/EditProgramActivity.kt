package com.example.rawan.radio.editProgram.view

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.rawan.radio.R
import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.addProgram.model.RadioPreviewAdapter
import com.example.rawan.radio.editProgram.model.EditProgramModel
import com.example.rawan.radio.editProgram.model.EditProgramViewModel
import com.example.rawan.radio.editProgram.presenter.EditProgramPresenter
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadioAdapter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.example.rawan.radio.searchForRadio.view.SearchForRadioActivity
import kotlinx.android.synthetic.main.activity_edit_program.*
import kotlinx.android.synthetic.main.activiy_add_program.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class EditProgramActivity : AppCompatActivity(),EditProgramView, LifecycleOwner {

    private val REQUEST_CODE_OF_RADIOS=1
    private var selectedList:List<Int> = listOf()
    private var favoriteSelected=0
    var programName:String = ""
    private var listOfExsitedAndChosenRadiosModel :MutableList<RadioProgramFromTo>?= mutableListOf()
    private var listOfExistedRadiosModel :MutableList<RadioProgramFromTo>?= mutableListOf()
    private lateinit var editProgramViewModel:EditProgramViewModel
    private lateinit var  listOfProgramRadioAdapter : ListOfProgramRadioAdapter
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var editProgramPresenter: EditProgramPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_program)
         programName = intent.extras["programName"].toString()
         val programImage = intent.extras["programImage"].toString()
        if (programImage.isNotEmpty()){
        imageChosenEditProgram.setImageBitmap(loadImageFromStorage(programImage,programName))
            imageChosenEditProgram.background = null
        }

        editProgramToolbar.title= "Edit $programName"

        editProgramPresenter= EditProgramPresenter(EditProgramModel(RadioDatabase.getInstance(this)),
                this,programName)
        editProgramPresenter.selectSelectedDaysOfProgram(programName)
        editProgramPresenter.selectFavorite(programName)
        editProgramViewModel =
            ViewModelProviders.of(this).get(EditProgramViewModel::class.java)
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.CREATED)
        editProgramViewModel.selectListOfProgramRadio(programName).observe(
            this, Observer {
        RVEditProgram.layoutManager = LinearLayoutManager(this)
        listOfProgramRadioAdapter = ListOfProgramRadioAdapter(it, this,clickListener = {true})
        RVEditProgram.adapter = listOfProgramRadioAdapter
            it?.map {
              val radios= RadioProgramFromTo(
                        it.radioName,it.radioImage,it.fromHour,
                        it.toHour,listOf(),"",""
                )
                this.listOfExsitedAndChosenRadiosModel?.add(radios)
                listOfExistedRadiosModel?.add(radios)
            }
        })
        favoriteEditProgram.setOnClickListener {
            if (favoriteEditProgram.background.constantState == getDrawable(R.drawable.ic_favorite_black_24dp).constantState){
                favoriteEditProgram.background=null
                favoriteEditProgram.background=getDrawable(R.drawable.ic_favorite_border_black_24dp)
            }
            else{
                favoriteEditProgram.background=null
                favoriteEditProgram.background=getDrawable(R.drawable.ic_favorite_black_24dp)
            }
        }
        btnAddChannelEditProgram.setOnClickListener {
            val intent = Intent(this, SearchForRadioActivity::class.java)
            intent.putExtra("listOfRadios",listOfExsitedAndChosenRadiosModel?.toTypedArray())
            startActivityForResult(intent,REQUEST_CODE_OF_RADIOS)
        }
        btnCancelEditProgram.setOnClickListener {
            finish()
        }
        btnCreateEditProgram.setOnClickListener {
            val listOfDaysToBeEnabled= mutableListOf<Int>()
            weekdaysPickerEditProgram.selectedDays.map {
                if(!selectedList.contains(it)){
                    listOfDaysToBeEnabled.add(it)
                }
            }

            selectedList.map {
                if(!weekdaysPickerEditProgram.selectedDays.contains(it))
                    editProgramPresenter.disable(it, programName)
            }
            if (favoriteEditProgram.background.constantState == getDrawable(R.drawable.ic_favorite_black_24dp).constantState&&favoriteSelected==0)
                editProgramPresenter.updateFav(1
                        , programName)

            else if (favoriteEditProgram.background.constantState == getDrawable(R.drawable.ic_favorite_border_black_24dp).constantState&&favoriteSelected==1)
                editProgramPresenter.updateFav(0, programName)
            val radiosTobeAdded= mutableListOf<RadioProgramFromTo>()
            listOfExsitedAndChosenRadiosModel?.map {
                //filter which radio was already existed
                if(!listOfExistedRadiosModel?.contains(it)!!){
                   radiosTobeAdded.add(it)
                }
            }
            editProgramPresenter.selectFromToDay(listOfDaysToBeEnabled,programName,
                    radiosTobeAdded,listOfExsitedAndChosenRadiosModel,weekdaysPickerEditProgram.selectedDays  )
            Toast.makeText(this,"Updated",Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CODE_OF_RADIOS
                &&data?.extras?.getParcelableArray("listOfRadios")?.toList()?.isNotEmpty()==true) {
            listOfExsitedAndChosenRadiosModel = data.extras?.getParcelableArray("listOfRadios")?.toMutableList() as? MutableList<RadioProgramFromTo>
            RVEditProgram.layoutManager = LinearLayoutManager(this)
            if (listOfExsitedAndChosenRadiosModel?.isNotEmpty() == true) {
                val radioPreviewAdapter = RadioPreviewAdapter(listOfExsitedAndChosenRadiosModel,
                        this,clickListener = {
                                     true
                })
                RVEditProgram.adapter = radioPreviewAdapter
            }
        }
    }
    override fun favorite(favorite: Int) {
        if (favorite==1) {
            favoriteSelected=1
            favoriteEditProgram.background=null
            favoriteEditProgram.background=getDrawable(R.drawable.ic_favorite_black_24dp)
        }
    }

    override fun selectedDays(listOfSelectedDays: List<Int>) {
        weekdaysPickerEditProgram.selectedDays=listOfSelectedDays
        selectedList=listOfSelectedDays
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }
    private fun loadImageFromStorage(path: String,programName:String) : Bitmap?{

        try {
            val f = File(path, "$programName.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            return b
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}

package com.example.rawan.radio.editProgram.view

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.rawan.radio.R
import com.example.rawan.radio.addProgram.model.RadioPreviewAdapter
import com.example.rawan.radio.editProgram.model.EditProgramModel
import com.example.rawan.radio.editProgram.model.EditProgramViewModel
import com.example.rawan.radio.editProgram.presenter.EditProgramPresenter
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadioAdapter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.example.rawan.radio.searchForRadio.view.SearchForRadioActivity
import kotlinx.android.synthetic.main.activity_edit_program.*
import kotlinx.android.synthetic.main.activiy_add_program.*
import java.io.*
import java.util.concurrent.TimeUnit

class EditProgramActivity : AppCompatActivity(),EditProgramView, LifecycleOwner {

    private val REQUEST_CODE_OF_RADIOS=1
    private var selectedList:List<Int> = listOf()
    private var favoriteSelected=0
    private var imageData: Bitmap? = null
    private val PICK_IMAGE = 27
    private val CHOOSE_IMAGE_REQUEST = 25
    var userChoseNewImage=false
    var programName:String = ""
    var imagePath:String=""
    private var listOfExistedAndChosenRadiosModel :MutableList<RadioProgramFromTo>?= mutableListOf()
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
        imageChosenEditProgram.setOnClickListener {
            startDialog()
        }
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
        var textToBeShare="Here is the list of radios that i listen to frequently!"
        editProgramViewModel.selectListOfProgramRadio(programName).observe(
            this, Observer {listOfRadioProgram->
        RVEditProgram.layoutManager = LinearLayoutManager(this)
        listOfProgramRadioAdapter = ListOfProgramRadioAdapter(listOfRadioProgram, this,clickListener = {true})
           listOfRadioProgram?.forEach {
               textToBeShare += "Radio Name:" +it.radioName+"\nfrom "+ TimeUnit.MILLISECONDS.toMinutes(it.fromHour).div(60)+":"+ (TimeUnit.MILLISECONDS.toMinutes(it.fromHour)%60)+
                    " to "+TimeUnit.MILLISECONDS.toMinutes(it.toHour).div(60)+
                    ":"+ (TimeUnit.MILLISECONDS.toMinutes(it.toHour)%60)+"\n"
           }
            shareProgramWithFriends.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        textToBeShare)
                startActivity(Intent.createChooser(sendIntent,"Share Program"))
            }
            RVEditProgram.adapter = listOfProgramRadioAdapter
            //add existed radios in database to two lists, the first listOfExistedRadiosModel which contain static data,
            //the second listOfExistedAndChosenRadiosModel contain both database radios stations and selected ones, then the compared to add new items to database.
            listOfRadioProgram?.map {
              val radios= RadioProgramFromTo(
                        it.radioName,it.radioImage,it.fromHour,
                        it.toHour,listOf(),"",""
                )
                this.listOfExistedAndChosenRadiosModel?.add(radios)
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
            intent.putExtra("listOfRadios",listOfExistedAndChosenRadiosModel?.toTypedArray())
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
            listOfExistedAndChosenRadiosModel?.map {
                //filter which radio was already existed
                if(!listOfExistedRadiosModel?.contains(it)!!){
                   radiosTobeAdded.add(it)
                }
            }
            if (userChoseNewImage)
                imagePath=saveToInternalStorage(imageData!!,programName)

            editProgramPresenter.selectFromToDay(userChoseNewImage,imagePath,listOfDaysToBeEnabled,programName,
                    radiosTobeAdded,listOfExistedAndChosenRadiosModel,weekdaysPickerEditProgram.selectedDays  )
            Toast.makeText(this,"Updated",Toast.LENGTH_LONG).show()
            finish()
        }
    }
    private fun saveToInternalStorage(bitmapImage: Bitmap,radioName:String): String {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("Radio", Context.MODE_APPEND)
        // Create imageDir
        val myPath = File(directory, "$radioName.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return directory.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CODE_OF_RADIOS
                &&data?.extras?.getParcelableArray("listOfRadios")?.toList()?.isNotEmpty()==true) {
            listOfExistedAndChosenRadiosModel = data.extras?.getParcelableArray("listOfRadios")?.toMutableList() as? MutableList<RadioProgramFromTo>
            RVEditProgram.layoutManager = LinearLayoutManager(this)
            if (listOfExistedAndChosenRadiosModel?.isNotEmpty() == true) {
                val radioPreviewAdapter = RadioPreviewAdapter(listOfExistedAndChosenRadiosModel,
                        this,clickListener = {
                                     true
                })
                RVEditProgram.adapter = radioPreviewAdapter
            }
        }
        else if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imageData = MediaStore.Images.Media.getBitmap(this.contentResolver,
                        data?.data)
                imageChosenEditProgram.background = null
                imageChosenEditProgram.setImageBitmap(imageData)
                userChoseNewImage=true
            }
        }
        else  if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageData = data!!.extras.get("data") as? Bitmap
            imageChosenEditProgram.background = null
            imageChosenEditProgram.setImageBitmap(imageData)
            userChoseNewImage=true
        }
    }
    private fun startDialog() {
        val myAlertDialog = AlertDialog.Builder(
                this)
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")

        myAlertDialog.setPositiveButton("Gallery"
        ) { arg0, arg1 ->
            val intent = Intent()
            intent.type = getString(R.string.type_img)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_img)), PICK_IMAGE)

        }

        myAlertDialog.setNegativeButton("Camera"
        ) { arg0, arg1 ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CHOOSE_IMAGE_REQUEST)
        }
        myAlertDialog.show()
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

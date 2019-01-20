package com.example.rawan.radio.addProgram.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.rawan.radio.R
import com.example.rawan.radio.addProgram.model.AddProgramModel
import com.example.rawan.radio.addProgram.presenter.AddProgramPresenter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import kotlinx.android.synthetic.main.activiy_add_program.*
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.rawan.radio.addProgram.model.FromToDays
import com.example.rawan.radio.addProgram.model.RadioPreviewAdapter
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.example.rawan.radio.searchForRadio.view.SearchForRadioActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



/**
 * Created by rawan on 22/11/18.
 */
class AddProgramActivity:AppCompatActivity(),AddProgramView {
    override fun createProgram() {
        finish()
    }

    private val REQUEST_CODE_OF_RADIOS = 1
    private val PICK_IMAGE = 20
    private val CHOOSE_IMAGE_REQUEST = 30
    var list: List<RadioProgramFromTo>? = listOf()

    private lateinit var addProgramPresenter: AddProgramPresenter
    private var imageData: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activiy_add_program)
        weekdaysPicker.selectedDays.clear()
        addProgramToolbar.title = getString(R.string.activity_title)

        favorite.setOnClickListener {
            if (favorite.background.constantState == getDrawable(R.drawable.ic_favorite_black_24dp).constantState) {
                favorite.background = null
                favorite.background = getDrawable(R.drawable.ic_favorite_border_black_24dp)
            } else {
                favorite.background = null
                favorite.background = getDrawable(R.drawable.ic_favorite_black_24dp)
            }
        }
        imageChosen.setOnClickListener {
             startDialog()
        }
        btnCancel.setOnClickListener {
            finish()
        }
        btnCreate.setOnClickListener {
            if(weekdaysPicker.selectedDays.isNotEmpty())
                addProgramPresenter.selectFromToDay(weekdaysPicker.selectedDays)
            else
                Toast.makeText(this, "Please Add Days ", Toast.LENGTH_SHORT).show()
        }
        addProgramPresenter = AddProgramPresenter(AddProgramModel(RadioDatabase.getInstance(this)),
                this)

        btnAddChannel.setOnClickListener {
            val intent = Intent(this, SearchForRadioActivity::class.java)
            intent.putExtra("listOfRadios", list?.toTypedArray())
            startActivityForResult(intent, REQUEST_CODE_OF_RADIOS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imageData = MediaStore.Images.Media.getBitmap(this.contentResolver,
                        data?.data)
                imageChosen.background = null
                imageChosen.setImageBitmap(imageData)
            }
        }
        else  if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageData = data!!.extras.get("data") as? Bitmap
            imageChosen.background = null
            imageChosen.setImageBitmap(imageData)
        }
        else if (requestCode == REQUEST_CODE_OF_RADIOS
                && data?.extras?.getParcelableArray("listOfRadios")?.toList()?.isNotEmpty() == true) {
            list = data.extras?.getParcelableArray("listOfRadios")?.toList() as? List<RadioProgramFromTo>
            RVReviewItem.layoutManager = LinearLayoutManager(this)
            if (list?.isNotEmpty() == true) {
                val radioPreviewAdapter = RadioPreviewAdapter(list, this,
                        clickListener = {
                            true
                        })
                RVReviewItem.adapter = radioPreviewAdapter
            }
        }
    }

    override fun toast(message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun toastString(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun intervalsOfSpecificDay(fromToDays: List<FromToDays>) {
         var flag = true
            fromToDays.map { objectsFromDatabase ->
                list?.forEach {
                    if ((objectsFromDatabase.fromHour in it.fromHour..it.toHour) ||
                            (objectsFromDatabase.toHour in it.fromHour..it.toHour)||
                            it.fromHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour||
                            it.toHour in objectsFromDatabase.fromHour..objectsFromDatabase.toHour ) {
                        Toast.makeText(this, getString(R.string.warning_overlap_program), Toast.LENGTH_SHORT).show()
                        flag = false
                        return@map
                    }
                }
            }
        if (list?.isNotEmpty() == true&&flag) {
                if (etProgramName.text.isNullOrEmpty())
                    programNameLayout.error = getString(R.string.error_name)
                else if (imageData!=null) {
                    programNameLayout.isErrorEnabled = false
                    addProgramPresenter.addProgram(etProgramName.text.toString(),
                            saveToInternalStorage(imageData!!,etProgramName.text.toString()),
                            if (favorite.background.constantState == getDrawable(R.drawable.ic_favorite_black_24dp).constantState)
                                1
                            else
                                0
                            , weekdaysPicker.selectedDays,
                            list!!)
                } else if (imageData==null){
                    Toast.makeText(this, "please choose program image", Toast.LENGTH_SHORT).show()
                }
            } else if (list?.isNotEmpty() == false)
                Toast.makeText(this, "please Add radio", Toast.LENGTH_SHORT).show()

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
}
package com.example.rawan.radio.listOfProgramRadios.view

import android.arch.lifecycle.*
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.widget.Toast
import com.example.rawan.radio.R
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadioAdapter
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramRadioViewModel
import com.example.rawan.radio.listOfProgramRadios.model.ListOfProgramsRadiosModel
import com.example.rawan.radio.listOfProgramRadios.presenter.ListOfProgramsRadiosPresenter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import kotlinx.android.synthetic.main.activity_list_of_programs_radios.*
import kotlinx.android.synthetic.main.add_date_radio.view.*

class ListOfProgramsRadiosActivity : AppCompatActivity(),ListOfProgramsRadiosView,LifecycleOwner {


    private lateinit var presenter: ListOfProgramsRadiosPresenter
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var  mAlertDialog :AlertDialog
    private lateinit var listOfProgramRadioViewModel: ListOfProgramRadioViewModel
    private lateinit var  listOfProgramRadioAdapter :ListOfProgramRadioAdapter
    private var programName=""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_programs_radios)
        programName=intent.extras["programName"].toString()
        programRadioToolbar.title=programName
        presenter= ListOfProgramsRadiosPresenter(ListOfProgramsRadiosModel(RadioDatabase.getInstance(this)),
                this)
        listOfProgramRadioViewModel =
                ViewModelProviders.of(this).get(ListOfProgramRadioViewModel::class.java)
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
        listOfProgramRadioViewModel.selectListOfProgramRadio(intent.extras["programName"].toString()).observe(this,
        Observer {list->
            RVProgramRadio.layoutManager = LinearLayoutManager(this)
             listOfProgramRadioAdapter = ListOfProgramRadioAdapter(list, this,clickListener = {
                 fromToRadio->
                 val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_date_radio, null)
                 mDialogView.background = getDrawable(R.drawable.background_border)
                 val mBuilder = AlertDialog.Builder(this)
                         .setView(mDialogView)
                  mAlertDialog  = mBuilder.show()
                 mDialogView.btnConfirm.setOnClickListener {
                     val fromDateLong= (mDialogView.fromTimePicker.hour*60000*60).plus(mDialogView.fromTimePicker.minute*60000).toLong()
                     val toDateLong= (mDialogView.toTimePicker.hour*60000*60).plus(mDialogView.toTimePicker.minute*60000).toLong()
                     if (fromDateLong >= toDateLong)
                      Toast.makeText(this, getString(R.string.warning_after_to), Toast.LENGTH_SHORT).show()
                     else {
                         list?.forEach {
                         if (!it.equals(fromToRadio)) {
                             if (((fromDateLong in it.fromHour..it.toHour) ||
                                             (toDateLong in it.fromHour..it.toHour) ||
                                             it.fromHour in fromDateLong..toDateLong ||
                                             it.toHour in fromDateLong..toDateLong)) {
                                 Toast.makeText(this, getString(R.string.warning_overlap), Toast.LENGTH_SHORT).show()
                                 return@setOnClickListener
                             }
                         }

                     }
                         presenter.selectFromToDay(programName,fromDateLong,toDateLong,fromToRadio.programId,fromToRadio.radioId,fromToRadio.fromHour)
                     }

                 }
                 //cancel button click of custom layout
                 mDialogView.btnCancel.setOnClickListener {
                     //dismiss dialog
                     mAlertDialog.dismiss()
                 }
                 true
             })
            RVProgramRadio.adapter = listOfProgramRadioAdapter

        })
        val swipeHandler =  object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                when(direction){
                    4->{
                        val adapterPosition=  viewHolder?.adapterPosition
                        val radioProgramEntity = listOfProgramRadioAdapter.radioPreviewList()?.get(adapterPosition!!)
                        presenter.deleteRadioProgram(radioProgramEntity!!.radioId,radioProgramEntity.programId,radioProgramEntity.fromHour)
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(RVProgramRadio)
    }

        override fun getLifecycle(): Lifecycle {
            return mLifecycleRegistry
        }
    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }
    override fun dismiss() {
        mAlertDialog.dismiss()
    }
}


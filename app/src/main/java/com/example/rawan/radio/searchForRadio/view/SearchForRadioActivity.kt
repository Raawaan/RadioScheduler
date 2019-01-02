package com.example.rawan.radio.searchForRadio.view

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.rawan.radio.MapsActivity
import com.example.rawan.radio.R
import com.example.rawan.radio.requestRadio.StationInfo
import com.example.rawan.radio.requestRadio.StationsAdapter
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.example.rawan.radio.searchForRadio.model.SearchForRadioModel
import com.example.rawan.radio.searchForRadio.presenter.SearchForRadioPresenter
import kotlinx.android.synthetic.main.activity_search_for_radio.*
import kotlinx.android.synthetic.main.add_date_radio.view.*
import java.sql.Time
import java.util.*

class SearchForRadioActivity : AppCompatActivity(), SearchForRadioView {
    override fun errorMsg(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    val REQUEST_CODE_OF_COUNTRY_CODE = 2
    val REQUEST_CODE_OF_RADIOS =1
    var list:MutableList<RadioProgramFromTo> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun listOfStations(listOfStations: List<StationInfo>) {
        if (listOfStations.isNotEmpty()) {
            myRVItem.visibility = View.VISIBLE
            confirmListOfRadios.visibility = View.VISIBLE
            tvSearchForRadios.visibility = View.GONE

            val stationsAdapter = StationsAdapter(listOfStations, clickListener = { stationInfo ->
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_date_radio, null)
                mDialogView.background = getDrawable(R.drawable.background_border)
                val mBuilder = AlertDialog.Builder(this)
                        .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
//            //login button click of custom layout
                mDialogView.btnConfirm.setOnClickListener {
                    val fromDateLong= (mDialogView.fromTimePicker.hour*60000*60).plus(mDialogView.fromTimePicker.minute*60000).toLong()

                    val toDateLong= (mDialogView.toTimePicker.hour*60000*60).plus(mDialogView.toTimePicker.minute*60000).toLong()

                    when {
                        fromDateLong >= toDateLong -> Toast.makeText(this, getString(R.string.warning_after_to), Toast.LENGTH_SHORT).show()
                        list.isEmpty()-> {
                            list.add(RadioProgramFromTo(
                                    stationInfo.name,
                                    stationInfo.imgUrl,
                                    fromDateLong,
                                    toDateLong,
                                    stationInfo.stream,
                                    stationInfo.description,
                                    stationInfo.facebookUrl
                            ))
                            Toast.makeText(this, getString(R.string.station_added), Toast.LENGTH_SHORT).show()
                            mAlertDialog.dismiss()
                        }
                        else -> {
                            list.forEach {
                                if ((fromDateLong in it.fromHour..it.toHour) ||
                                                (toDateLong in it.fromHour..it.toHour)||
                                        it.fromHour in fromDateLong..toDateLong||
                                        it.toHour in fromDateLong..toDateLong  ) {
                                    Toast.makeText(this, getString(R.string.warning_overlap), Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }
                            }

                            list.add(RadioProgramFromTo(
                                    stationInfo.name,
                                    stationInfo.imgUrl,
                                    fromDateLong,
                                    toDateLong,
                                    stationInfo.stream,
                                    stationInfo.description,
                                    stationInfo.facebookUrl
                            ))
                            Toast.makeText(this,getString(R.string.station_added) , Toast.LENGTH_SHORT).show()
                            mAlertDialog.dismiss()

                        }
                    }
                }
                //cancel button click of custom layout
                mDialogView.btnCancel.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                }
                true
            })
            myRVItem.adapter = stationsAdapter
            confirmListOfRadios.setOnClickListener {
                val intent = Intent()
                if(list.size>0) {
                    intent.putExtra("listOfRadios", list.toTypedArray())
                setResult(REQUEST_CODE_OF_RADIOS, intent)
                finish()
                }
                else
                    Toast.makeText(this,getString(R.string.add_radios), Toast.LENGTH_SHORT).show()

            }

        }
    }

   private lateinit var searchForRadioPresenter: SearchForRadioPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_radio)
        searchForRadioPresenter = SearchForRadioPresenter(SearchForRadioModel(), this)
        myRVItem.layoutManager = LinearLayoutManager(this)
        ivSelectLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_OF_COUNTRY_CODE)
        }
        if (intent?.extras?.getParcelableArray("listOfRadios")?.toList()?.isNotEmpty() == true) {
            val s = intent?.extras?.getParcelableArray("listOfRadios")?.toList() as? List<RadioProgramFromTo>
            s?.map {
                list.add(it)
            }
        }
        if(intent?.extras?.get("countryCode").toString()== "null"){
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_OF_COUNTRY_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OF_COUNTRY_CODE&&
                (data?.extras?.get("countryCode").toString() != "null")) {
            searchForRadioPresenter.requestRadios(data?.extras?.get("countryCode").toString())
        }
        else
            Toast.makeText(this, getString(R.string.choose_location), Toast.LENGTH_SHORT).show()

    }
}

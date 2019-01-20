package com.example.rawan.radio.home.view

import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Toast
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.example.rawan.radio.listOfProgramRadios.view.ListOfProgramsRadiosActivity
import com.example.rawan.radio.R
import com.example.rawan.radio.editProgram.view.EditProgramActivity
import com.example.rawan.radio.home.model.HomeViewModel
import com.example.rawan.radio.home.model.HomeModel
import com.example.rawan.radio.home.model.ProgramAndRadioProgram
import com.example.rawan.radio.home.model.ProgramsListAdapter
import com.example.rawan.radio.home.presenter.RequestAPIPresenter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.requestRadio.StreamsItem
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import com.example.rawan.radio.main.view.MainActivity


class HomeFragment : Fragment(),HomeView, LifecycleOwner {

    private lateinit var requestAPIPresenter: RequestAPIPresenter
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var viewModel: HomeViewModel
    private lateinit var  programsListAdapter:ProgramsListAdapter
    companion object {
        fun newInstance():HomeFragment {
            return HomeFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestAPIPresenter= RequestAPIPresenter(HomeModel(RadioDatabase.getInstance(this.context)),this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        RVProgramsList.layoutManager = LinearLayoutManager(this.context)
        viewModel.selectProgramAndRadioProgramByFav().observe(this, Observer {listOfPrograms->
            viewModelObservable(listOfPrograms)
        })
        val swipeHandler =  object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                when(direction){
                    4->{
                        val adapterPosition=  viewHolder?.adapterPosition
                        requestAPIPresenter.deleteProgram(programsListAdapter.listOfPrograms()?.get(adapterPosition!!)!!.programName)
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(RVProgramsList)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }
    override fun toast(msg: String) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
    }
    private fun viewModelObservable(listOfPrograms: List<ProgramAndRadioProgram>?) {
        if (listOfPrograms?.isNotEmpty() == true) {
            tVEmptyList.visibility = View.GONE
            RVProgramsList.visibility = View.VISIBLE
            programsListAdapter = ProgramsListAdapter(listOfPrograms, this.context, clickListener = {
                val intent = Intent(context, ListOfProgramsRadiosActivity::class.java)
                intent.putExtra("programName", it.programName)
                startActivity(intent)
                true
            },
                    clickListenerEditImageView = {
                        val intent = Intent(context, EditProgramActivity::class.java)
                        intent.putExtra("programName", it.programName)
                        intent.putExtra("programImage", it.programImage)

                        startActivity(intent)
                        true
                    })
            RVProgramsList.adapter = programsListAdapter
        } else {
            tVEmptyList?.visibility = View.VISIBLE
            RVProgramsList.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.favorite -> {
                viewModel.selectProgramAndRadioProgramByFav().observe(this, Observer {listOfPrograms->
                    viewModelObservable(listOfPrograms)
                })
                return true
            }
            R.id.alphabetical -> {
                viewModel.selectProgramAndRadioProgramByName().observe(this, Observer {listOfPrograms->
                    viewModelObservable(listOfPrograms)
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

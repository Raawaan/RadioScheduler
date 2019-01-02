package com.example.rawan.radio.home.view

import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
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
import com.example.rawan.radio.home.model.ProgramsListAdapter
import com.example.rawan.radio.home.presenter.RequestAPIPresenter
import com.example.rawan.radio.main.view.FragmentClickListener
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.requestRadio.StreamsItem
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import com.example.rawan.radio.main.view.MainActivity


class HomeFragment : Fragment(),HomeView, LifecycleOwner,FragmentClickListener {
    private lateinit var requestAPIPresenter: RequestAPIPresenter
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    private lateinit var viewModel: HomeViewModel
    private lateinit var  programsListAdapter:ProgramsListAdapter
    companion object {
        fun newInstance():HomeFragment {
            return HomeFragment()
        }
    }

    override fun toAlphabetical() {
        viewModel.selectProgramAndRadioProgramByName().observe(this, Observer {listOfPrograms->
            if(listOfPrograms?.isNotEmpty()==true){
                tVEmptyList.visibility=View.GONE
                RVProgramsList.visibility=View.VISIBLE
                programsListAdapter=ProgramsListAdapter(listOfPrograms,this.context,clickListener = {
                    val intent =Intent(context, ListOfProgramsRadiosActivity::class.java)
                    intent.putExtra("programName",it.programName)
                    startActivity(intent)
                    true
                },
                        clickListenerEditImageView = {
                            val intent =Intent(context, EditProgramActivity::class.java)
                            intent.putExtra("programName",it.programName)
                            startActivity(intent)
                            true
                        })
                RVProgramsList.adapter = programsListAdapter
            }
            else{
                tVEmptyList?.visibility=View.VISIBLE
                RVProgramsList.visibility=View.GONE
            }
        })
    }

    override fun toFavorite() {
        viewModel.selectProgramAndRadioProgramByFav().observe(this, Observer {listOfPrograms->
            if(listOfPrograms?.isNotEmpty()==true){
                tVEmptyList.visibility=View.GONE
                RVProgramsList.visibility=View.VISIBLE
                programsListAdapter=ProgramsListAdapter(listOfPrograms,this.context,clickListener = {
                    val intent =Intent(context, ListOfProgramsRadiosActivity::class.java)
                    intent.putExtra("programName",it.programName)
                    startActivity(intent)
                    true
                },
                        clickListenerEditImageView = {
                            val intent =Intent(context, EditProgramActivity::class.java)
                            intent.putExtra("programName",it.programName)
                            startActivity(intent)
                            true
                        })
                RVProgramsList.adapter = programsListAdapter
            }
            else{
                tVEmptyList?.visibility=View.VISIBLE
                RVProgramsList.visibility=View.GONE
            }
        })

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (activity as MainActivity).setOnClickListener(this)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestAPIPresenter= RequestAPIPresenter(HomeModel(RadioDatabase.getInstance(this.context)),this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        RVProgramsList.layoutManager = LinearLayoutManager(this.context)
        viewModel.selectProgramAndRadioProgramByFav().observe(this, Observer {listOfPrograms->
            if(listOfPrograms?.isNotEmpty()==true){
            tVEmptyList.visibility=View.GONE
             RVProgramsList.visibility=View.VISIBLE
             programsListAdapter=ProgramsListAdapter(listOfPrograms,this.context,clickListener = {
                 val intent =Intent(context, ListOfProgramsRadiosActivity::class.java)
                 intent.putExtra("programName",it.programName)
                 startActivity(intent)
                 true
             },
                     clickListenerEditImageView = {
                         val intent =Intent(context, EditProgramActivity::class.java)
                         intent.putExtra("programName",it.programName)
                         startActivity(intent)
                         true
                     })
                RVProgramsList.adapter = programsListAdapter
            }
            else{
                tVEmptyList?.visibility=View.VISIBLE
                RVProgramsList.visibility=View.GONE
            }
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

}

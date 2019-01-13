package com.example.rawan.radio.home.model

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rawan.radio.R
import kotlinx.android.synthetic.main.list_of_programs.view.*
import android.os.Build
import android.support.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.preview_list.view.*
import java.util.concurrent.TimeUnit
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**
 * Created by rawan on 28/11/18.
 */
class ProgramsListAdapter(private var programsList: List<ProgramAndRadioProgram>?, val context: Context,
                          private val clickListener:(ProgramAndRadioProgram)->Boolean,
                          private val clickListenerEditImageView:(ProgramAndRadioProgram)->Boolean)
    : RecyclerView.Adapter<ProgramsListAdapter.ProgramsListHolderView>() {
    open class ProgramsListHolderView(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(programAndRadioProgram: ProgramAndRadioProgram, clickListener: (ProgramAndRadioProgram) -> Boolean,
                 clickListenerEditImageView:(ProgramAndRadioProgram)->Boolean){
            itemView.setOnClickListener {
                clickListener(programAndRadioProgram)
            }
            itemView.ivEditProgram.setOnClickListener {
                clickListenerEditImageView(programAndRadioProgram)
            }
        }
    }
    fun listOfPrograms():List<ProgramAndRadioProgram>?{
        notifyDataSetChanged()
        return programsList
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ProgramsListHolderView, position: Int) {
        val program = programsList?.get(position)
        program?.let {
            holder.bind(it,clickListener,clickListenerEditImageView)
        }
        holder.itemView.programNameList.text = program?.programName
        holder.itemView.tvToListOfPrograms.text =
                context.getString(com.example.rawan.radio.R.string.timeFormatTo,
                        TimeUnit.MILLISECONDS.toMinutes(program?.to!!).div(60).toString(),
                        (TimeUnit.MILLISECONDS.toMinutes(program.to)%60).toString())
        holder.itemView.tvFromListOfPrograms.text =
                context.getString(com.example.rawan.radio.R.string.timeFormatFrom,

                        TimeUnit.MILLISECONDS.toMinutes(program.from).div(60).toString(),
                        (TimeUnit.MILLISECONDS.toMinutes(program.from)%60).toString())
        if (loadImageFromStorage(program.programImage,program.programName) !=null){
            holder.itemView.programImageList.setImageBitmap(loadImageFromStorage(program.programImage,program.programName))
        }
        else
        holder.itemView.programImageList.setImageResource(com.example.rawan.radio.R.drawable.ic_image_black_24dp)
        if (program.favorite == 1)
            holder.itemView.favoriteListOfPrograms.setImageResource(com.example.rawan.radio.R.drawable.ic_favorite_black_24dp)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramsListHolderView {
        return ProgramsListHolderView(LayoutInflater.from(parent.context)
                .inflate(com.example.rawan.radio.R.layout.list_of_programs, parent, false))
    }

    private fun loadImageFromStorage(path: String,programName:String) :Bitmap?{

        try {
            val f = File(path, "$programName.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
           return b
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getItemCount() = programsList!!.size
}
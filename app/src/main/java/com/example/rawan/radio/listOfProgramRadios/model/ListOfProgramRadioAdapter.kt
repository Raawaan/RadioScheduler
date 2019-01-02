package com.example.rawan.radio.listOfProgramRadios.model

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rawan.radio.R
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.preview_list.view.*
import java.util.concurrent.TimeUnit

/**
 * Created by rawan on 30/11/18.
 */
class ListOfProgramRadioAdapter(private val listOfProgramRadio: List<ListOfProgramRadio>?, val context: Context,
                                private val clickListener:(ListOfProgramRadio)->Boolean)
    : RecyclerView.Adapter<ListOfProgramRadioAdapter.ListOfProgramRadioViewHolder>(){
    open class ListOfProgramRadioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(listOfProgramRadio: ListOfProgramRadio, clickListener: (ListOfProgramRadio) -> Boolean){
            itemView.setOnClickListener {
                clickListener(listOfProgramRadio)
            }
        }
    }
    fun radioPreviewList():List<ListOfProgramRadio>?{
        notifyDataSetChanged()
        return listOfProgramRadio
    }
    override fun onBindViewHolder(holder: ListOfProgramRadioViewHolder, position: Int) {
        val radioPreview = listOfProgramRadio?.get(position)
        radioPreview?.let { holder.bind(it,clickListener) }
        holder.itemView.tvFromPreview.text=context.getString(R.string.timeFormatFrom
                , TimeUnit.MILLISECONDS.toMinutes(radioPreview?.fromHour!!).div(60).toString(),
                (TimeUnit.MILLISECONDS.toMinutes(radioPreview.fromHour)%60).toString())
        holder.itemView.tvToPreview.text=context.getString(R.string.timeFormatTo,
                TimeUnit.MILLISECONDS.toMinutes(radioPreview.toHour).div(60).toString(),
                (TimeUnit.MILLISECONDS.toMinutes(radioPreview.toHour)%60).toString())
        holder.itemView.radioNamePreview.text=context.getString(R.string.radioName,radioPreview?.radioName)
        if(radioPreview.radioImage!=null)
            Picasso.get().load(radioPreview.radioImage).fit().centerCrop().into(holder.itemView.radioLogoPreview)
        else
            holder.itemView.radioLogoPreview.setImageResource(R.drawable.ic_image_black_24dp)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOfProgramRadioViewHolder {
        return ListOfProgramRadioViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.preview_list, parent, false))
    }
    override fun getItemCount() = listOfProgramRadio!!.size
}
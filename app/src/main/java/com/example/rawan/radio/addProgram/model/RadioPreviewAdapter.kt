package com.example.rawan.radio.addProgram.model

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rawan.radio.R
import com.example.rawan.radio.requestRadio.StationInfo
import com.example.rawan.radio.searchForRadio.model.RadioProgramFromTo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.preview_list.view.*
import java.util.concurrent.TimeUnit

/**
 * Created by rawan on 26/11/18.
 */
class RadioPreviewAdapter(private val radioPreviewList: List<RadioProgramFromTo>?,val context: Context,
                         private val clickListener:(RadioProgramFromTo)->Boolean)
    : RecyclerView.Adapter<RadioPreviewAdapter.RadioPreviewHolderView>(){
    open class RadioPreviewHolderView(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(radioPreviewList: RadioProgramFromTo, clickListener: (RadioProgramFromTo) -> Boolean){
            itemView.setOnClickListener {
                clickListener(radioPreviewList)
            }
        }
    }

    override fun onBindViewHolder(holder: RadioPreviewHolderView, position: Int) {
        val radioPreview = radioPreviewList?.get(position)
        radioPreview?.let { holder.bind(it,clickListener) }
        holder.itemView.tvFromPreview.text=context.getString(R.string.timeFormatFrom,
                TimeUnit.MILLISECONDS.toMinutes(radioPreview?.fromHour!!).div(60).toString(),
                (TimeUnit.MILLISECONDS.toMinutes(radioPreview.fromHour)%60).toString())
        holder.itemView.tvToPreview.text=context.getString(R.string.timeFormatTo,
                TimeUnit.MILLISECONDS.toMinutes(radioPreview.toHour).div(60).toString(),
                (TimeUnit.MILLISECONDS.toMinutes(radioPreview.toHour)%60).toString())
        holder.itemView.radioNamePreview.text=context.getString(R.string.radioName,radioPreview?.name)
        if(radioPreview.image!=null)
            Picasso.get().load(radioPreview.image).fit().centerCrop().into(holder.itemView.radioLogoPreview)
        else
            holder.itemView.radioLogoPreview.setImageResource(R.drawable.ic_image_black_24dp)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioPreviewHolderView {
        return RadioPreviewHolderView(LayoutInflater.from(parent.context)
                .inflate(R.layout.preview_list, parent, false))
    }
    override fun getItemCount() = radioPreviewList!!.size
}
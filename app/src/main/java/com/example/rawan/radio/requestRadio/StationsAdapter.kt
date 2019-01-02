package com.example.rawan.radio.requestRadio

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rawan.radio.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_of_stations.view.*

/**
 * Created by rawan on 13/11/18.
 */
class StationsAdapter(private val stationsList: List<StationInfo>?,
                      val clickListener:(StationInfo)->Boolean)
    : RecyclerView.Adapter<StationsAdapter.StationHolderView>(){
    open class StationHolderView(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(stationInfo: StationInfo, clickListener: (StationInfo) -> Boolean){
            itemView.setOnClickListener {
                clickListener(stationInfo)
            }
        }
    }
    override fun onBindViewHolder(holder: StationHolderView, position: Int) {
        val listOfStations = stationsList?.get(position)
        listOfStations?.let { holder.bind(it,clickListener) }
        holder.itemView.tvStationName.text=listOfStations?.name
        if(listOfStations?.imgUrl!=null)
        Picasso.get().load(listOfStations.imgUrl).fit().centerCrop().into(holder.itemView.ivStationImage)
        else
            holder.itemView.ivStationImage.setImageResource(R.drawable.ic_image_black_24dp)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolderView {
        return StationHolderView(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_of_stations, parent, false))
    }
    override fun getItemCount() = stationsList!!.size
}
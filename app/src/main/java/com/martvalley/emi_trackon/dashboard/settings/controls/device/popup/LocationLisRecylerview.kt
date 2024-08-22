package com.martvalley.emi_trackon.dashboard.settings.controls.device.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.martvalley.emi_trackon.databinding.LocationListItemBinding
import com.martvalley.emi_trackon.utils.viewInGoogleMaps

class LocationLisRecylerview(val context: Context,var mlist: List<LocationData>) :
    RecyclerView.Adapter<LocationLisRecylerview.ViewHolder>() {

    fun setData(list:List<LocationData>){
        this.mlist = list
    }

    inner class ViewHolder(val binding: LocationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LocationData) {
            binding.latTxt.text = data.lat.toString()
            binding.lonTxt.text = data.lon.toString()

            binding.viewOnMap.setOnClickListener {
                viewInGoogleMaps(data.lat.toString(),data.lon.toString(),context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LocationListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mlist[position])
    }

}
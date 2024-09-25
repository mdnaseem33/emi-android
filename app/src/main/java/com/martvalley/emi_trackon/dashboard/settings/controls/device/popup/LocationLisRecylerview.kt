package com.martvalley.emi_trackon.dashboard.settings.controls.device.popup

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.martvalley.emi_trackon.databinding.LocationListItemBinding
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.viewInGoogleMaps
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class LocationLisRecylerview(val context: Context,var mlist: List<LocationData>) :
    RecyclerView.Adapter<LocationLisRecylerview.ViewHolder>() {

    fun setData(list:List<LocationData>){
        this.mlist = list
    }

    inner class ViewHolder(val binding: LocationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LocationData) {
            data.logd("datetime")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.latTxt.text = convertStringToDateTime(data.created_at!!).format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"))
            }else{
                binding.latTxt.text = data.created_at!!
            }

            binding.viewOnMap.setOnClickListener {
                viewInGoogleMaps(data.lat.toString(),data.lon.toString(),context)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToDateTime(dateTimeString: String): LocalDateTime {
        val offsetDateTime = OffsetDateTime.parse(dateTimeString)
        return offsetDateTime.toLocalDateTime().plusHours(5).plusMinutes(30)
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
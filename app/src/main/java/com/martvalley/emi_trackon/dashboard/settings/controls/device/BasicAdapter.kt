package com.martvalley.emi_trackon.dashboard.settings.controls.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.settings.controls.Control
import com.martvalley.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.martvalley.emi_trackon.databinding.ControlBasicItemBinding

class BasicAdapter(
    var mList: ArrayList<Control.DeviceActionOld>,
    val context: Context,
    val listner: (Int, String, Control.DeviceActionOld) -> Unit,


    ) : RecyclerView.Adapter<BasicAdapter.ViewHolder>() {

    var isClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ControlBasicItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ControlBasicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Control.DeviceActionOld) {

            binding.tv.text = data.display

            if ((context as ControlsActivity).selectedList == "list1") {
                if (data.value) {
                    binding.tv.backgroundTintList =
                        ColorStateList.valueOf(context.getColor(R.color.green))
                    binding.tv.setTextColor(context.getColor(R.color.white))
                } else {
                    binding.tv.backgroundTintList = null
                    binding.tv.setTextColor(context.getColor(R.color.blue))
                }
            }

            if (data.display == "Audio" || data.display == "Location" ||
                data.display == "Call List" || data.display == "Mobile No.") {
                binding.tv.backgroundTintList = null
                binding.tv.setTextColor(context.getColor(R.color.blue))
            }

            if (data.sm == "ip" || data.sm == "uip" || data.sm == "fr" || data.sm == "uftd" ){
                if (data.value){
                    binding.tv.backgroundTintList = null
                    binding.tv.setTextColor(context.getColor(R.color.blue))
                } else {
                    binding.tv.backgroundTintList =
                        ColorStateList.valueOf(context.getColor(R.color.green))
                    binding.tv.setTextColor(context.getColor(R.color.white))
                }

            }

            itemView.setOnClickListener {
                data.value = !data.value
                notifyItemChanged(adapterPosition)

                if (data.list == "list1") {
                    listner(adapterPosition, "list1", data)
                    Log.d("ListnerTrueFA", data.toString())

                } else {
                    listner(adapterPosition, data.display, data)
                    Log.d("ListnerTrueFA", data.toString())
                }


//                if (data.value) {
//                    Log.d("ListnerTrueFA",data.value.toString())
//                    binding.tv.backgroundTintList =
//                        ColorStateList.valueOf(context.getColor(R.color.green))
//                    binding.tv.setTextColor(context.getColor(R.color.white))
//                } else {
//                    binding.tv.backgroundTintList = null
//                    binding.tv.setTextColor(context.getColor(R.color.blue))
//                }
                // isClicked =! isClicked

            }
        }
    }
}
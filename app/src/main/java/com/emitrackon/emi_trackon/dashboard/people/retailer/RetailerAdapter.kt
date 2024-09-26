package com.emitrackon.emi_trackon.dashboard.people.retailer

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.databinding.RetailerItemBinding

class RetailerAdapter(
    var mList: ArrayList<Retailer.User>,
    val context: Context,
    val listner: (Retailer.User, String, Int) -> Unit,
) :
    RecyclerView.Adapter<RetailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RetailerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: RetailerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Retailer.User, position: Int) {

            binding.id.text = data.id.toString()
            binding.nameValueTv.text = data.name
            binding.phoneValueTv.text = data.phone
            binding.emailValueTv.text = data.email
            binding.stateValueTv.text = data.state
            binding.balanceValueTv.text = data.balance.toString()

            binding.statusBtn.text = when (data.status) {
                0 -> "inactive"
                1 -> "active"
                else -> ""
            }

            if (data.status == 1) {
                binding.statusBtn.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.green))
            } else {
                binding.statusBtn.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.red))
            }

            binding.statusBtn.setOnClickListener { listner(data, "action", position) }
            binding.more.setOnClickListener { listner(data, "more", position) }
            itemView.setOnClickListener { listner(data, "detail", position) }

        }
    }
}
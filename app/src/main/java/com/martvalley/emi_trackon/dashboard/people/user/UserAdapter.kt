package com.martvalley.emi_trackon.dashboard.people.user

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.people.retailer.Retailer
import com.martvalley.emi_trackon.databinding.UserItemBinding
import com.martvalley.emi_trackon.utils.convertISOTimeToDate
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show

class UserAdapter(
    var mList: ArrayList<User.Customer>,
    val context: Context,
    val listner: (User.Customer, String, Int) -> Unit,
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(
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

    inner class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: User.Customer) {

            binding.id.text = data.id.toString()
            binding.name.text = data.name ?: ""
            binding.imei1Value.text = data.imei1 ?: ""
            binding.imei2Value.text = data.imei2 ?: ""
            binding.createdValue.text = data.created_at.convertISOTimeToDate()
            binding.syncValue.text = data.last_sync?.split(" ")?.get(0) ?: ""

            if (data.is_link == "0") {
                binding.statusBtn.text = "Show QR"
                binding.statusBtn.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.blue))
            } else {
                binding.statusBtn.text = when (data.status) {
                    0 -> "Surrendered"
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
            }

            itemView.setOnClickListener { listner(data, "control", adapterPosition) }

            binding.statusBtn.setOnClickListener {
                if (data.is_link == "0") {
                    context.startActivity(
                        Intent(context, UserQrActivity::class.java).putExtra(
                            "id",
                            data.id.toString()
                        )
                    )
                }
            }
            binding.more.setOnClickListener { listner(data, "more", adapterPosition) }

        }
    }
}
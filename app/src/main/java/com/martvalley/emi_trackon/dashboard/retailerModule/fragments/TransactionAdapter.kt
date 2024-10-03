package com.martvalley.emi_trackon.dashboard.retailerModule.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.TransactionItemBinding

class TransactionAdapter(var mList: ArrayList<AllTransactionData.Transaction>, val user_id: Int?) :  RecyclerView.Adapter<TransactionAdapter.ViewHolder>(){

    inner class  ViewHolder(val binding: TransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AllTransactionData.Transaction, user_id: Int?) {
            if(data.sender_id == user_id){
                binding.typeTextView.text = "Debit"
                binding.typeTextView.setTextColor(itemView.context.resources.getColor(R.color.red))
            }
            binding.senderTextView.text = "Sender: ${data.sender.name}"
            binding.receiverTextView.text = "Receiver: ${data.reciever.name}"
            binding.amountTextView.text = "${data.amount}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TransactionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], user_id)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
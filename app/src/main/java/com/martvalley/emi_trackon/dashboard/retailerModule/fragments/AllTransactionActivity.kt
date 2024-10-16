package com.martvalley.emi_trackon.dashboard.retailerModule.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.retailerModule.fragments.AllTransactionData.Transaction
import com.martvalley.emi_trackon.databinding.ActivityAllTransactionBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllTransactionActivity : AppCompatActivity() {
    val binding by lazy { ActivityAllTransactionBinding.inflate(layoutInflater) }
    lateinit var adapter: TransactionAdapter
    var list = ArrayList<Transaction>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.text.text = "All Transactions"
        binding.toolbar.arrow.setOnClickListener { finish() }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllTransactionActivity)
        }
        val user_id = SharedPref(MainApplication.appContext).getValueInt(Constants.USERID)
        adapter = TransactionAdapter(list, user_id)
        binding.recyclerView.adapter = adapter
        withNetwork { callApi() }
    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getAllTransaction()
        call.enqueue(object : Callback<AllTransactionData.TransactionResponse> {
            override fun onResponse(
                call: Call<AllTransactionData.TransactionResponse>,
                response: Response<AllTransactionData.TransactionResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.transactions)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showToast("Oops! There is a problem connecting to the server. Please try again")
                    }
                }
            }

            override fun onFailure(
                call: Call<AllTransactionData.TransactionResponse>, t: Throwable
            ) {
                binding.pb.hide()
                showToast("Oops! There is a problem connecting to the server. Please try again")
            }

        })

    }
}
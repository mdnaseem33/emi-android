package com.martvalley.emi_trackon.dashboard.retailerModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.databinding.ActivityWhatsNewBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatsNewActivity : AppCompatActivity() {
    val binding by lazy { ActivityWhatsNewBinding.inflate(layoutInflater) }
    lateinit var adapter: WhatsNewAdapter
    var list = ArrayList<WhatsNewData.WhatsNewRequestItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.text.text = "What's New"
        binding.toolbar.arrow.setOnClickListener { finish() }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@WhatsNewActivity)
        }
        adapter = WhatsNewAdapter(list)
        binding.recyclerView.adapter = adapter
        withNetwork { callApi() }
    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getWhatsAll()
        call.enqueue(object : Callback<WhatsNewData.WhatsNewRequest> {
            override fun onResponse(
                call: Call<WhatsNewData.WhatsNewRequest>,
                response: Response<WhatsNewData.WhatsNewRequest>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showToast("Oops! There is a problem connecting to the server. Please try again")
                    }
                }
            }

            override fun onFailure(
                call: Call<WhatsNewData.WhatsNewRequest>, t: Throwable
            ) {
                binding.pb.hide()
                showToast("Oops! There is a problem connecting to the server. Please try again")
            }

        })

    }
}
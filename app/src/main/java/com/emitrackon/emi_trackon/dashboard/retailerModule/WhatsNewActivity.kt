package com.emitrackon.emi_trackon.dashboard.retailerModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.dashboard.people.user.User
import com.emitrackon.emi_trackon.databinding.ActivityWhatsNewBinding
import com.emitrackon.emi_trackon.utils.hide
import com.emitrackon.emi_trackon.utils.show
import com.emitrackon.emi_trackon.utils.showToast
import com.emitrackon.emi_trackon.utils.withNetwork
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
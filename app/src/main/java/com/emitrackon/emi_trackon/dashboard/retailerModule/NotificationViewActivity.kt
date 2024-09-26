package com.emitrackon.emi_trackon.dashboard.retailerModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.databinding.ActivityNotificationViewBinding
import com.emitrackon.emi_trackon.databinding.ActivityUpcomingEmiBinding
import com.emitrackon.emi_trackon.utils.hide
import com.emitrackon.emi_trackon.utils.show
import com.emitrackon.emi_trackon.utils.showToast
import com.emitrackon.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewActivity : AppCompatActivity() {
    val binding by lazy { ActivityNotificationViewBinding.inflate(layoutInflater) }
    lateinit var adapter: NotificationAdapter
    var list = ArrayList<NotificationData.Notify>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.text.text = "Notifications"
        binding.toolbar.arrow.setOnClickListener { finish() }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationViewActivity)
        }
        adapter = NotificationAdapter(list)
        binding.recyclerView.adapter = adapter
        withNetwork { callApi() }
    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getNotificationAll()
        call.enqueue(object : Callback<NotificationData.NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationData.NotificationResponse>,
                response: Response<NotificationData.NotificationResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.data)
                            adapter.notifyDataSetChanged()
                            readApi()
                        }
                    }

                    else -> {
                        showToast("Oops! There is a problem connecting to the server. Please try again")
                    }
                }
            }

            override fun onFailure(
                call: Call<NotificationData.NotificationResponse>, t: Throwable
            ) {
                binding.pb.hide()
                showToast("Oops! There is a problem connecting to the server. Please try again")
            }

        })

    }

    private fun readApi() {

        val call = RetrofitInstance.apiService.readNotificationAll()
        call.enqueue(object : Callback<NotificationData.ReadNotification> {
            override fun onResponse(
                call: Call<NotificationData.ReadNotification>,
                response: Response<NotificationData.ReadNotification>
            ) {


            }

            override fun onFailure(
                call: Call<NotificationData.ReadNotification>, t: Throwable
            ) {

            }

        })

    }
}
package com.martvalley.emi_trackon.dashboard.people.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.people.retailer.Retailer
import com.martvalley.emi_trackon.dashboard.people.retailer.RetailerAdapter
import com.martvalley.emi_trackon.databinding.ActivityChooseRetailerBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseUserActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChooseRetailerBinding.inflate(layoutInflater) }
    private lateinit var adapter: ChooseUserAdapter
    val list = ArrayList<User.Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.text.text = "Choose user"
        binding.toolbar.calender.hide()
        binding.toolbar.filter.hide()
        binding.toolbar.arrow.setOnClickListener { onBackPressed() }

        adapter = ChooseUserAdapter(list, this) { data, pos ->
            setResult(RESULT_OK, Intent().putExtra("id", data.id))
            finish()
        }
        binding.rv.adapter = adapter

        withNetwork { callApi() }

    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getCustomerListApi()
        call.enqueue(object : Callback<User.UserListResponse> {
            override fun onResponse(
                call: Call<User.UserListResponse>,
                response: Response<User.UserListResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.customer)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<User.UserListResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }


}
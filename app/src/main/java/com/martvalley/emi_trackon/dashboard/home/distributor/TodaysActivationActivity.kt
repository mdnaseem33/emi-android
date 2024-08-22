package com.martvalley.emi_trackon.dashboard.home.distributor

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.people.retailer.Retailer
import com.martvalley.emi_trackon.dashboard.people.retailer.RetailerViewActivity
import com.martvalley.emi_trackon.databinding.ActivityActiveUsersBinding
import com.martvalley.emi_trackon.databinding.ActivityDashboardBinding
import com.martvalley.emi_trackon.databinding.ActivityTodaysActivationBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodaysActivationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTodaysActivationBinding.inflate(layoutInflater) }
    private lateinit var adapter: DistributorTodaysActivationAdapter
    val list = ArrayList<Dashboard.TodaysActivation>()

    val vieww =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("statuschanged", false) == true) {
                    withNetwork { callApi() }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.text.text = "Today's Activation"
        binding.toolbar.arrow.setOnClickListener { onBackPressed() }
        binding.toolbar.filter.hide()
        binding.toolbar.calender.hide()


        adapter = DistributorTodaysActivationAdapter(list, this) { data, action, pos ->
            when (action) {
                "action" -> {
                    callChangeStatusApi(
                        data.id.toString(),
                        data.status.toString(),
                        pos
                    )
                }
                "detail" -> {
                    vieww.launch(
                        Intent(
                            this,
                            RetailerViewActivity::class.java
                        ).putExtra("id", data.id)
                    )
                }
            }
        }

        binding.rv.adapter = adapter

        binding.clearSearch.setOnClickListener {
            binding.searchEt.text.clear()
            adapter.mList = list
            adapter.notifyDataSetChanged()
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterList(p0.toString().lowercase())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        withNetwork { callApi() }

    }


    private fun filterList(key: String) {
        val filter_list = ArrayList<Dashboard.TodaysActivation>()
        list.forEach {
            if (it.name.lowercase().contains(key)) {
                filter_list.add(it)
            }
        }
        adapter.mList = filter_list
        adapter.notifyDataSetChanged()
    }

    private fun callApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getDistributorTodayActivationListApi()
        call.enqueue(object : Callback<Dashboard.TodaysActivationDistributorResponse> {
            override fun onResponse(
                call: Call<Dashboard.TodaysActivationDistributorResponse>,
                response: Response<Dashboard.TodaysActivationDistributorResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list.clear()
                            list.addAll(it.todays_activation_list)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(
                call: Call<Dashboard.TodaysActivationDistributorResponse>,
                t: Throwable
            ) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }

    private fun callChangeStatusApi(id: String, status: String, pos: Int) {
        binding.pb.show()
        val status_value = if (status == "0") 1 else 0
        val request = Retailer.StatusChangeRequest(id, status_value.toString())
        val call = RetrofitInstance.apiService.retailerStatusChangeApi(request)
        call.enqueue(object : Callback<Retailer.StatusChangeResponse> {
            override fun onResponse(
                call: Call<Retailer.StatusChangeResponse>,
                response: Response<Retailer.StatusChangeResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            list[pos].status = status_value
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Retailer.StatusChangeResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }


}


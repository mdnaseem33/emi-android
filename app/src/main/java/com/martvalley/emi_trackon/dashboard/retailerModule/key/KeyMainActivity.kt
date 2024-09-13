package com.martvalley.emi_trackon.dashboard.retailerModule.key

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.RetailerViewModel
import com.martvalley.emi_trackon.dashboard.retailerModule.key.model.CreateCustomerData
import com.martvalley.emi_trackon.utils.withNetwork
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class KeyMainActivity : AppCompatActivity() {
    private val viewModel:RetailerViewModel by viewModels()
    private var createCustomerData : CreateCustomerData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_main)
        withNetwork { getCreateData() }
    }

    private fun getCreateData(){
        val call = RetrofitInstance.apiService.getCustomerCreateData()
        call.enqueue(object : Callback<CreateCustomerData> {
            override fun onResponse(
                p0: Call<CreateCustomerData>,
                p1: Response<CreateCustomerData>
            ) {
                createCustomerData = p1.body()
            }

            override fun onFailure(p0: Call<CreateCustomerData>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }



    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@KeyMainActivity, DashBoardNewActivity::class.java))
        finish()
    }

}
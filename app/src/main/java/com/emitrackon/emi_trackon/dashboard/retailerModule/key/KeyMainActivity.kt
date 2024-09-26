package com.emitrackon.emi_trackon.dashboard.retailerModule.key

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.RetailerViewModel
import com.emitrackon.emi_trackon.dashboard.retailerModule.key.model.CreateCustomerData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeyMainActivity : AppCompatActivity() {
    private val viewModel:RetailerViewModel by viewModels()
    private var createCustomerData : CreateCustomerData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_main)
        //withNetwork { getCreateData() }
    }

//    private fun getCreateData(){
//        val call = RetrofitInstance.apiService.getCustomerCreateData()
//        call.enqueue(object : Callback<CreateCustomerData> {
//            override fun onResponse(
//                p0: Call<CreateCustomerData>,
//                p1: Response<CreateCustomerData>
//            ) {
//                createCustomerData = p1.body()
//            }
//
//            override fun onFailure(p0: Call<CreateCustomerData>, p1: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }



    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@KeyMainActivity, DashBoardNewActivity::class.java))
        finish()
    }

}
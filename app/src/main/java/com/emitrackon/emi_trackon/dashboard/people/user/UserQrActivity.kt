package com.emitrackon.emi_trackon.dashboard.people.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.databinding.ActivityUserQrBinding
import com.emitrackon.emi_trackon.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserQrActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUserQrBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.text.text = "Customer Qr Code"
        binding.toolbar.arrow.setOnClickListener { onBackPressed() }
        binding.toolbar.calender.hide()
        binding.toolbar.filter.hide()


        if(intent.getBooleanExtra("local_image", false)){
            binding.qr.setImageResource(R.drawable.app_qr_test)
            binding.id.text = intent.getStringExtra("id").toString()
        }else{
            withNetwork { callApi() }
        }

    }

    private fun callApi() {
        binding.pb.show()
        val call =
            RetrofitInstance.apiService.getCustomerQRApi(intent.getStringExtra("id").toString())
        call.enqueue(object : Callback<User.QRResponse> {
            override fun onResponse(
                call: Call<User.QRResponse>, response: Response<User.QRResponse>
            ) {
                binding.pb.hide()
                Log.d("TAG", "onResponse: ${response.body()}")
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            binding.qr.setImageBitmap(it.qr.base64ToBitmap())
                            binding.id.text = intent.getStringExtra("id").toString()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<User.QRResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }

}
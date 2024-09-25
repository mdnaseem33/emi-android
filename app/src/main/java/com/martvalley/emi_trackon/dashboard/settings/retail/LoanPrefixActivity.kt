package com.martvalley.emi_trackon.dashboard.settings.retail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.settings.Settings
import com.martvalley.emi_trackon.databinding.ActivityLoanPrefixBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.logd
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoanPrefixActivity : AppCompatActivity() {
    val binding by lazy { ActivityLoanPrefixBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        MainApplication.authData?.loan_prefix?.let {
            it.logd()
            if (it != "") {
                binding.msgEt.setText(it)
            }
        }

        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }

        binding.backImage.setOnClickListener {
            this.onBackPressed()
        }

        binding.saveButton.setOnClickListener {
            val msg = binding.msgEt.text.trim().toString()
            if (msg.isEmpty()) {
                showToast("Enter Frp Email")
            } else {
                withNetwork { callUploadMediaApi( Settings.SaveLoanPrefixRequest(msg) ) }
            }
        }
    }
    private fun callUploadMediaApi(request: Settings.SaveLoanPrefixRequest) {
        request.logd()
        binding.pb.show()
        val call = RetrofitInstance.apiService.saveLoanPrefixApi(request)
        call.enqueue(object : Callback<Settings.SaveWallpaperResponse> {
            override fun onResponse(
                call: Call<Settings.SaveWallpaperResponse>,
                response: Response<Settings.SaveWallpaperResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            showToast(it.message)
                            MainApplication.authData!!.loan_prefix = request.loan_prefix
                        }
                    }

                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Settings.SaveWallpaperResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }
}
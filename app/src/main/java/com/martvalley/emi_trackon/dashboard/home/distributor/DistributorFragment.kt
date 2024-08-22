package com.martvalley.emi_trackon.dashboard.home.distributor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.people.MyQrActivity
import com.martvalley.emi_trackon.dashboard.qr_code.QrFragment
import com.martvalley.emi_trackon.databinding.FragmentDistributorBinding
import com.martvalley.emi_trackon.databinding.FragmentRetailorBinding
import com.martvalley.emi_trackon.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistributorFragment : Fragment() {

    private val binding by lazy { FragmentDistributorBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        withNetwork { callDashboardApi() }

        binding.activation.img.setImageResource(R.drawable.activation)
        binding.users.img.setImageResource(R.drawable.people_img)
        binding.activeUsers.img.setImageResource(R.drawable.check_user_circle)
        binding.creditUsed.img.setImageResource(R.drawable.box)
        binding.creditAvailable.img.setImageResource(R.drawable.package_box)

        binding.activation.tv.text = "TODAYS ACTIVATION"
        binding.users.tv.text = "TOTAL USERS"
        binding.activeUsers.tv.text = "ACTIVE USERS"
        binding.creditUsed.tv.text = "CREDIT USED"
        binding.creditAvailable.tv.text = "CREDIT AVAILABLE"

        binding.copy.setOnClickListener {
            context?.let { it1 ->
                val textToCopy = SharedPref(it1).getValueInt(Constants.USERID).toString()
                it1.copyToClipboard(textToCopy)
            }
        }

        binding.qr.setOnClickListener {
            context?.startActivity(Intent(context, MyQrActivity::class.java))
        }
        binding.activation.root.setOnClickListener {
            context?.startActivity(Intent(context, TodaysActivationActivity::class.java))
        }
        binding.activeUsers.root.setOnClickListener {
            context?.startActivity(Intent(context, ActiveUsersActivity::class.java))
        }
//        binding.creditUsed.root.setOnClickListener {
//            context?.startActivity(Intent(context, CreditUsedActivity::class.java))
//        }
        binding.users.root.setOnClickListener {
            context?.startActivity(Intent(context, TotalDistributorsActivity::class.java))
        }

    }


    private fun callDashboardApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getDistributorDashboardApi()
        call.enqueue(object : Callback<Dashboard.DistributorResponse> {
            override fun onResponse(
                call: Call<Dashboard.DistributorResponse>,
                response: Response<Dashboard.DistributorResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            binding.activation.numbers.text = it.todays_activation.toString()
                            binding.users.numbers.text = it.total_retailer.toString()
                            binding.activeUsers.numbers.text = it.active_retailer.toString()
                            binding.creditUsed.numbers.text = it.credit_used.toString()
                            binding.creditAvailable.numbers.text = it.credit_available.toString()

                            binding.id.text = MainApplication.authData?.qr_id.toString()
//                                    SharedPref(it1).getValueInt(Constants.USERID).toString()
                        }
                    }
                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Dashboard.DistributorResponse>, t: Throwable) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })

    }


}
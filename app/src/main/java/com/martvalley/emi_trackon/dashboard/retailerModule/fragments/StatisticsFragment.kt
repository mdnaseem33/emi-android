package com.martvalley.emi_trackon.dashboard.retailerModule.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.home.retailer.RetailerActiveUsersActivity
import com.martvalley.emi_trackon.dashboard.home.retailer.TotalRetailersActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.martvalley.emi_trackon.databinding.FragmentStatisticsBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.creditTransLayout.setOnClickListener {
            startActivity(Intent(requireContext(), AllTransactionActivity::class.java))
        }


        callDashboardApi()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun callDashboardApi() {
        binding.pb.show()
        val call = RetrofitInstance.apiService.getRetailerDashboardApi()
        call.enqueue(object : Callback<Dashboard.RetailerResponse> {
            override fun onResponse(
                call: Call<Dashboard.RetailerResponse>,
                response: Response<Dashboard.RetailerResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            binding.pb.hide()
                            binding.totalUserCount.text = it.total_costomer
                            binding.totalActiveUserCount.text = it.active_costomer
                            binding.usedCreditCount.text = it.credit_used
                            binding.totalCreditAvailCount.text = it.credit_available
                            if(SharedPref(requireContext()).getValueInt(Constants.SUB_ROLE) == 2){
                                binding.totalUserCount.text = it.total_retailer
                                binding.nameTotal.text = "Total Retailers"
                                binding.totalActiveUserCount.text = it.retailer_active
                                binding.nameActive.text = "Active Retailers"
                                binding.totalUserLayout.setOnClickListener {
                                    try {
                                        (requireActivity() as DashBoardNewActivity).changeNav(R.id.people_retailer)
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                }

                                binding.activeUserLayout.setOnClickListener {
                                    try {
                                        (requireActivity() as DashBoardNewActivity).changeNav(R.id.people_retailer)
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                }
                            }else{
                                binding.totalUserLayout.setOnClickListener {
                                    startActivity(Intent(context, TotalRetailersActivity::class.java))
                                }

                                binding.activeUserLayout.setOnClickListener {
                                    startActivity(Intent(context, RetailerActiveUsersActivity::class.java))
                                }
                            }

                        }
                    }
                    else -> {
                        context?.showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Dashboard.RetailerResponse>, t: Throwable) {
                binding.pb.hide()
                context?.showApiErrorToast()
            }

        })

    }
}
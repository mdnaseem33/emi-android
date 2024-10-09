package com.martvalley.emi_trackon.dashboard.settings.retail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.retailerModule.ChatBotActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.martvalley.emi_trackon.dashboard.settings.*
import com.martvalley.emi_trackon.databinding.FragmentRetailerSettingBinding
import com.martvalley.emi_trackon.login.Auth
import com.martvalley.emi_trackon.login.LoginActivity
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.loadImage
import com.martvalley.emi_trackon.utils.showApiErrorToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetailerSettingFragment : Fragment() {

    private val binding by lazy { FragmentRetailerSettingBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.msg.img.setImageResource(R.drawable.img)
        binding.wallpaper.img.setImageResource(R.drawable.wallpaper_customize)
        binding.password.img.setImageResource(R.drawable.change_password)
        binding.profile.img.setImageResource(R.drawable.edit_profile)
        binding.logout.img.setImageResource(R.drawable.logout)
        binding.support.img.setImageResource(R.drawable.live_support_img)
        binding.loanPrefix.img.setImageResource(R.drawable.baseline_assured_workload_24)
        binding.switcher.img.setImageResource(R.drawable.baseline_swap_horiz_24)
        binding.switcher.root.visibility = View.GONE
        binding.msg.tv.text = "Change Profile Image"
        binding.wallpaper.tv.text = "Wallpaper Customize"
        binding.password.tv.text = "Change Password"
        binding.profile.tv.text = "Edit Profile"
        binding.logout.tv.text = "Logout"
        binding.support.tv.text = "Live Support!"
        binding.frp.tv.text = "Custom FRP Email"
        binding.qrCode.tv.text = "Payment QR"
        binding.loanPrefix.tv.text = "Loan Prefix"

        //binding.audio.tv.text = "Audio Customize"
        binding.switcher.root.setOnClickListener {
            SharedPref(requireContext()).save(Constants.IS_RETAILER, 1)
            startActivity(Intent(requireContext(), DashBoardNewActivity::class.java))

        }
        binding.support.root.setOnClickListener {
            startActivity(Intent(requireContext(), ChatBotActivity::class.java))
        }

        binding.msg.root.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileImageActivity::class.java))
        }
        binding.wallpaper.root.setOnClickListener {
            startActivity(Intent(requireContext(), WallpaperCustomiseActivity::class.java))
        }

        binding.logout.root.setOnClickListener {
            SharedPref(requireContext()).clearSharedPreference()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }

        binding.password.root.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }
        binding.profile.root.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        typeUI()
        callAuthApi()
    }

    private fun typeUI(){
        if(SharedPref(requireContext()).getValueInt(Constants.SUB_ROLE) == 2 || SharedPref(requireContext()).getValueInt(Constants.IS_RETAILER) == 2){
            binding.frp.root.visibility = View.GONE
            binding.loanPrefix.root.visibility = View.GONE
            binding.wallpaper.root.visibility = View.GONE
            binding.wallpaper.root.visibility = View.GONE
            binding.report.plusMember.visibility = View.GONE

        }else{
            binding.frp.root.setOnClickListener {
                startActivity(Intent(requireContext(), FrpEmailActivity::class.java))
            }
            binding.loanPrefix.root.setOnClickListener {
                startActivity(Intent(requireContext(), LoanPrefixActivity::class.java))
            }

            binding.qrCode.root.setOnClickListener {
                startActivity(Intent(requireContext(), PaymentQr::class.java))
            }
        }

        if(SharedPref(requireContext()).getValueInt(Constants.SUB_ROLE) == 3){
            if(SharedPref(requireContext()).getValueInt(Constants.IS_RETAILER) == 2){
                binding.switcher.tv.text = "Switch To Retailer"
            }else if(SharedPref(requireContext()).getValueInt(Constants.IS_RETAILER) == 1){
                binding.switcher.tv.text = "Switch To Distributor"
            }else{
                SharedPref(requireContext()).save(Constants.IS_RETAILER, 2)
                binding.switcher.tv.text = "Switch To Distributor"
            }

            binding.switcher.root.visibility = View.VISIBLE
        }
    }

    private fun callAuthApi() {
        val call = RetrofitInstance.apiService.getAuthApi()
        call.enqueue(object : Callback<Auth.AuthResponse> {
            override fun onResponse(
                call: Call<Auth.AuthResponse>, response: Response<Auth.AuthResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            binding.report.userNameTextView.text = it.name
                            binding.report.userPhoneTextView.text = it.phone
                            binding.report.plusMember.text = it.member
                            if (it.image != null) {
                                val imageUrl = Constants.BASEURL + "storage/public/" +it.image
                                Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(binding.report.imageViewProfile);
                                binding.msg.img.loadImage(imageUrl)
                            }

                        }
                    }
                    else -> {
                        requireContext().showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Auth.AuthResponse>, t: Throwable) {
                requireContext().showApiErrorToast()
            }

        })

    }

    override fun onResume() {
        super.onResume()
        callAuthApi()
    }
}
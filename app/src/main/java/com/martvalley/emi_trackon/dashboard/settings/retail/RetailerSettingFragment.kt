package com.martvalley.emi_trackon.dashboard.settings.retail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.retailerModule.ChatBotActivity
import com.martvalley.emi_trackon.dashboard.settings.*
import com.martvalley.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.martvalley.emi_trackon.dashboard.settings.report.ReportActivity
import com.martvalley.emi_trackon.databinding.FragmentRetailerSettingBinding
import com.martvalley.emi_trackon.login.Auth
import com.martvalley.emi_trackon.login.LoginActivity
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.openWhatsAppConversationUsingUri
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

       // binding.report.img.setImageResource(R.drawable.report)
        binding.msg.img.setImageResource(R.drawable.lock_messages)
        binding.wallpaper.img.setImageResource(R.drawable.wallpaper_customize)
        binding.password.img.setImageResource(R.drawable.change_password)
        binding.profile.img.setImageResource(R.drawable.edit_profile)
        binding.logout.img.setImageResource(R.drawable.logout)
        binding.support.img.setImageResource(R.drawable.live_support_img)
        //binding.audio.img.setImageResource(R.drawable.baseline_audio_file_24)

      //  binding.report.tv.text = "Report"
        binding.msg.tv.text = "Lock Message"
        binding.wallpaper.tv.text = "Wallpaper Customize"
        binding.password.tv.text = "Change Password"
        binding.profile.tv.text = "Edit Profile"
        binding.logout.tv.text = "Logout"
        binding.support.tv.text = "Live Support!"
        //binding.audio.tv.text = "Audio Customize"


//        binding.support.root.setOnClickListener {
//            context?.let { it1 -> openWhatsAppConversationUsingUri(it1, "+912269646511", "") }
//        }

        binding.support.root.setOnClickListener {
            startActivity(Intent(requireContext(), ChatBotActivity::class.java))
        }

        binding.report.root.setOnClickListener {
            startActivity(Intent(requireContext(), ReportActivity::class.java))
        }
        binding.msg.root.setOnClickListener {
            startActivity(Intent(requireContext(), LockMessageActivity::class.java))
        }
        binding.wallpaper.root.setOnClickListener {
            startActivity(Intent(requireContext(), WallpaperCustomiseActivity::class.java))
        }

//        binding.audio.root.setOnClickListener {
//            startActivity(Intent(requireContext(), AudioCustomizeActivity::class.java))
//        }

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
        callAuthApi()
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
                            if (it.image != null) {
                                val imageUrl = Constants.BASEURL + "storage/public/" +it.image
                                Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(binding.report.imageViewProfile);
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
}
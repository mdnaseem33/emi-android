package com.martvalley.emi_trackon.dashboard.settings.retail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.settings.*
import com.martvalley.emi_trackon.dashboard.settings.controls.ControlsActivity
import com.martvalley.emi_trackon.dashboard.settings.report.ReportActivity
import com.martvalley.emi_trackon.databinding.FragmentRetailerSettingBinding
import com.martvalley.emi_trackon.login.LoginActivity
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.openWhatsAppConversationUsingUri

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


        binding.support.root.setOnClickListener {
            context?.let { it1 -> openWhatsAppConversationUsingUri(it1, "+912269646511", "") }
        }

//        binding.report.root.setOnClickListener {
//            startActivity(Intent(requireContext(), ReportActivity::class.java))
//        }
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
    }
}
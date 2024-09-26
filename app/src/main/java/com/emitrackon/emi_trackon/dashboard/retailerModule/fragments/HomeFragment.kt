package com.emitrackon.emi_trackon.dashboard.retailerModule.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.dashboard.adapter.HomePagerAdapter
import com.emitrackon.emi_trackon.dashboard.adapter.VideoAdapter
import com.emitrackon.emi_trackon.dashboard.home.Dashboard
import com.emitrackon.emi_trackon.dashboard.home.retailer.RetailerActiveUsersActivity
import com.emitrackon.emi_trackon.dashboard.home.retailer.RetailerTodaysActivationActivity
import com.emitrackon.emi_trackon.dashboard.home.retailer.TotalRetailersActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.ChatBotActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.TutorialActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.UpcomingEmiActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.WhatsNewActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.key.KeyMainActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.key.SmartKey
import com.emitrackon.emi_trackon.databinding.FragmentHomeBinding
import com.emitrackon.emi_trackon.utils.hide
import com.emitrackon.emi_trackon.utils.show
import com.emitrackon.emi_trackon.utils.showApiErrorToast
import com.emitrackon.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2
    private val notificationCountListener: NotificationCountListener by lazy { activity as NotificationCountListener }
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPager2 = binding.viewPager2
        val keyLayout = binding.keyIncludedLayout
        //Pager Adapter

        withNetwork { callDashboardApi() }

        keyLayout.smartKeyCard.setOnClickListener {
            val intent = Intent(requireContext(), SmartKey::class.java)
            intent.putExtra("title", "Smart Key")
            intent.putExtra("sub_title", "Mobile FRP Protection")
            startActivity(intent)
        }
        keyLayout.superKeyCard.setOnClickListener {
            val intent = Intent(requireContext(), SmartKey::class.java)
            intent.putExtra("title", "Super Key")
            intent.putExtra("sub_title", "Zero Touch Enrollment")
            startActivity(intent)
        }

        keyLayout.homeAppCard.setOnClickListener {
            val intent = Intent(requireContext(), SmartKey::class.java)
            intent.putExtra("title", "Home Appliance")
            intent.putExtra("sub_title", "Install without reset device")
            startActivity(intent)
        }

        keyLayout.udharCard.setOnClickListener {
            val intent = Intent(requireContext(), KeyMainActivity::class.java)
            intent.putExtra("Value_Key", "Udhar")
            startActivity(intent)
        }

        binding.explore.todayActivation.setOnClickListener {
            startActivity(Intent(requireContext(), RetailerTodaysActivationActivity::class.java))
        }

        binding.explore.totalCustomer.setOnClickListener {
            startActivity(Intent(context, TotalRetailersActivity::class.java))
        }

        binding.explore.helpSupport.setOnClickListener {
            startActivity(Intent(requireContext(), ChatBotActivity::class.java))
        }

        binding.explore.activeCustomer.setOnClickListener {
            startActivity(Intent(context, RetailerActiveUsersActivity::class.java))
        }

        binding.explore.videoTutorials.setOnClickListener {
            startActivity(Intent(context, TutorialActivity::class.java))
        }

        binding.explore.whatNew.setOnClickListener {
            startActivity(Intent(context, WhatsNewActivity::class.java))
        }

        binding.explore.upcomingEmi.setOnClickListener {
            startActivity(Intent(requireContext(), UpcomingEmiActivity::class.java))
        }

        return binding.root
    }



    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
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
                            val adapter = HomePagerAdapter(it.bannerList, requireContext())
                            viewPager2.adapter = adapter
                            binding.wormDotsIndicator.attachTo(viewPager2)
                            binding.youtubeLinks.layoutManager = LinearLayoutManager(requireContext());
                            binding.youtubeLinks.adapter = VideoAdapter(requireContext(), it.youtubeLinks)
                            if(it.notificationCount != null && it.notificationCount > 0){
                                notificationCountListener.onNotificationCountUpdated(it.notificationCount)
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

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        _binding = null
    }
}
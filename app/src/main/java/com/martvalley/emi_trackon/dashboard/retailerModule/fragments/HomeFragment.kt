package com.martvalley.emi_trackon.dashboard.retailerModule.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.adapter.HomePagerAdapter
import com.martvalley.emi_trackon.dashboard.home.Dashboard
import com.martvalley.emi_trackon.dashboard.people.user.UserQrActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.key.KeyMainActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.key.SmartKey
import com.martvalley.emi_trackon.databinding.FragmentHomeBinding
import com.martvalley.emi_trackon.utils.hide
import com.martvalley.emi_trackon.utils.show
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private val images = listOf(
        "https://img.freepik.com/premium-psd/horizontal-website-banne_451189-109.jpg?semt=ais_hybrid",
        "https://img.freepik.com/free-vector/realism-hand-drawn-horizontal-banner_23-2150203461.jpg?semt=ais_hybrid",
        "https://img.freepik.com/premium-psd/horizontal-website-banne_451189-109.jpg?semt=ais_hybrid",
        "https://static.vecteezy.com/system/resources/previews/002/179/542/original/sale-offer-banner-with-hand-holding-phone-vector.jpg",
        "https://img.freepik.com/free-vector/super-sale-phone-banner-mobile-clearance-sale-discount-poster-smartphone-sale-marketing-special-offer-promotion_433751-53.jpg"
    )

    companion object {
        const val URL = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPager2 = binding.viewPager2
        val keyLayout = binding.keyIncludedLayout
        //Pager Adapter
        val adapter = HomePagerAdapter(images)
        viewPager2.adapter = adapter
        binding.wormDotsIndicator.attachTo(viewPager2)
        preparePlayer()

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

        }

        return binding.root
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        exoPlayer?.playWhenReady = true
        binding.exoplayer.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem =
            MediaItem.fromUri(URL)
        val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
            .createMediaSource(mediaItem)
        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.seekTo(playbackPosition)
        exoPlayer?.playWhenReady = playWhenReady
        exoPlayer?.prepare()
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
//                            binding.activation.numbers.text = it.todays_activation.toString()
//                            binding.users.numbers.text = it.total_costomer.toString()
//                            binding.activeUsers.numbers.text = it.active_costomer.toString()
//                            binding.creditUsed.numbers.text = it.credit_used.toString()
//                            binding.creditAvailable.numbers.text = it.credit_available.toString()
//
//                            binding.retailerId.text = MainApplication.authData?.qr_id.toString()
//                            binding.retailerId.text =
//                                context?.let { it1 ->
//                                    SharedPref(it1).getValueInt(Constants.USERID).toString()
//                                }

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
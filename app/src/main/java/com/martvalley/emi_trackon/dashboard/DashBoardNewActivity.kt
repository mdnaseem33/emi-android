package com.martvalley.emi_trackon.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.viewpager2.widget.ViewPager2
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.adapter.AdapterKeyList
import com.martvalley.emi_trackon.dashboard.adapter.HomePagerAdapter
import com.martvalley.emi_trackon.dashboard.model.KeyModel
import com.martvalley.emi_trackon.databinding.ActivityDashboardNewBinding

class DashBoardNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardNewBinding
    private lateinit var viewPager2: ViewPager2

    private val images = listOf(
        "https://img.freepik.com/premium-psd/horizontal-website-banne_451189-109.jpg?semt=ais_hybrid",
        "https://img.freepik.com/free-vector/realism-hand-drawn-horizontal-banner_23-2150203461.jpg?semt=ais_hybrid",
        "https://img.freepik.com/premium-psd/horizontal-website-banne_451189-109.jpg?semt=ais_hybrid",
        "https://static.vecteezy.com/system/resources/previews/002/179/542/original/sale-offer-banner-with-hand-holding-phone-vector.jpg",
        "https://img.freepik.com/free-vector/super-sale-phone-banner-mobile-clearance-sale-discount-poster-smartphone-sale-marketing-special-offer-promotion_433751-53.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardNewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewPager2 = binding.viewPager2


        //Pager Adapter
        val adapter = HomePagerAdapter(images)
        viewPager2.adapter = adapter
        binding.wormDotsIndicator.attachTo(viewPager2)



    }

}
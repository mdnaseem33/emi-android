package com.martvalley.emi_trackon.dashboard.retailerModule.user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.ActivityUserDetailBinding

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
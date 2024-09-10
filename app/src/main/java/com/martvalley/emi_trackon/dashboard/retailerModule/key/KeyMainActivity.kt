package com.martvalley.emi_trackon.dashboard.retailerModule.key

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.martvalley.emi_trackon.dashboard.retailerModule.RetailerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeyMainActivity : AppCompatActivity() {
    private val viewModel:RetailerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_main)

//        onBackPressedDispatcher.onBackPressed().run {
//            startActivity(Intent(this@KeyMainActivity,DashBoardNewActivity::class.java))
//        }
    }

}
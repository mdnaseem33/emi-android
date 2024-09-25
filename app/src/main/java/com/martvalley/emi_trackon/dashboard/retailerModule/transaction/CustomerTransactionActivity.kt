package com.martvalley.emi_trackon.dashboard.retailerModule.transaction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.ActivityCustomerTransactionBinding

class CustomerTransactionActivity : AppCompatActivity() {
    private val binding: ActivityCustomerTransactionBinding by lazy { ActivityCustomerTransactionBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}
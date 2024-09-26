package com.emitrackon.emi_trackon.dashboard.retailerModule.transaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emitrackon.emi_trackon.databinding.ActivityCustomerTransactionBinding

class CustomerTransactionActivity : AppCompatActivity() {
    private val binding: ActivityCustomerTransactionBinding by lazy { ActivityCustomerTransactionBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}
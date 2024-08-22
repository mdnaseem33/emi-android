package com.martvalley.emi_trackon.dashboard.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.dashboard.home.retailer.CreateUserActivity
import com.martvalley.emi_trackon.databinding.ActivitySigningBinding

class SigningActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigningBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.clear.setOnClickListener {
          //  binding.signatureView.clearCanvas()
        }

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.tick.setOnClickListener {
            if (intent.getStringExtra("create") == "user") {
               // CreateUserActivity.bitmap = binding.signatureView.signatureBitmap
            } //else EditProfileActivity.bitmap = binding.signatureView.signatureBitmap
            setResult(RESULT_OK)
            finish()
        }

    }
}
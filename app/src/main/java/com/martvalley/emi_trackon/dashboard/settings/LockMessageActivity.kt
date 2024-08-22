package com.martvalley.emi_trackon.dashboard.settings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.databinding.ActivityLockMessageBinding
import com.martvalley.emi_trackon.databinding.ActivityUpdateBinding
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.showToast
import com.martvalley.emi_trackon.utils.withNetwork

class LockMessageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLockMessageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val msg = SharedPref(this).getValueString(Constants.MESSAGE)
        if (msg?.isNotEmpty() == true || msg != "") {
            binding.msgEt.setText(msg)
        }

        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }

        binding.backImage.setOnClickListener {
            onBackPressed()
        }

        binding.saveButton.setOnClickListener {
            val msg = binding.msgEt.text.trim().toString()
            if (msg.isEmpty()) {
                showToast("Enter message")
            } else {
                SharedPref(this).save(Constants.MESSAGE, msg)
                onBackPressed()
//                withNetwork { }
            }
        }
    }
}
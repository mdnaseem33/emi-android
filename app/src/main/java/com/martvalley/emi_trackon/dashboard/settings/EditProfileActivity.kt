package com.martvalley.emi_trackon.dashboard.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.people.retailer.Retailer
import com.martvalley.emi_trackon.databinding.ActivityEditProfileBinding
import com.martvalley.emi_trackon.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    companion object {
        var bitmap: Bitmap? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.text.text = "Edit Profile"
        binding.toolbar.arrow.setOnClickListener { onBackPressed() }
        binding.toolbar.filter.hide()
        binding.toolbar.calender.hide()

        setData()

        binding.saveButton.setOnClickListener {
            val address = binding.retailerAddressEt.text.trim().toString()
            val email = binding.retailerEmailEt.text.trim().toString()
            val gst = binding.retailerGstEt.text.trim().toString()
            val name = binding.retailerNameEt.text.trim().toString()
            val ownername = binding.retailerOwnernameEt.text.trim().toString()
            val state = binding.retailerStateEt.text.trim().toString()
            val phone = binding.retailerPhoneEt.text.trim().toString()

            if (name.isEmpty()) {
                showToast("Enter retailer name.")
            } else if (ownername.isEmpty()) {
                showToast("Enter owner name.")
            } else if (phone.isEmpty()) {
                showToast("Enter phone name.")
            } else if (email.isEmpty()) {
                showToast("Enter email address.")
            } else if (address.isEmpty()) {
                showToast("Enter full address.")
            } else if (state.isEmpty()) {
                showToast("Enter state.")
            } else if (gst.isEmpty()) {
                showToast("Enter gst.")
            } else if (!binding.checkbox.isChecked && bitmap != null) {
                showToast("Please tick sign here.")
            } else {
                val request = Settings.ProfileUpdateRequest(
                    address = address,
                    gst = gst,
                    name = name,
                    owner_name = ownername,
                    state = state,
                    sign = bitmap?.bitmapToBase64().toString()
                )
                callEditApi(request)
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (bitmap == null) {
                    showToast("Please sign to continue.")
                    compoundButton.isChecked = false
                }
            }
        }

        binding.checkbox.setSpan(this) {
            startActivityForResult(
                Intent(this, SigningActivity::class.java).putExtra("create", ""),
                10
            )
        }

    }

    private fun setData() {
        MainApplication.authData?.let {
            binding.retailerAddressEt.setText(it.address)
            binding.retailerEmailEt.setText(it.email)
            binding.retailerGstEt.setText(it.gst)
            binding.retailerNameEt.setText(it.name)
            binding.retailerOwnernameEt.setText(it.owner_name)
            binding.retailerStateEt.setText(it.state)
            binding.retailerPhoneEt.setText(it.phone)
            if (it.sign != "") {
                Glide.with(this).asBitmap().load(
                    "${Constants.BASEURL}storage/" + MainApplication.authData?.sign
                ).into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        binding.signView.setImageBitmap(resource)
                        bitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })

                binding.signView.show()
                binding.checkbox.isChecked = true
            }
        }
    }


    private fun callEditApi(request: Settings.ProfileUpdateRequest) {
        binding.pb.show()
        val call = RetrofitInstance.apiService.profileUpdateApi(request)
        call.enqueue(object : Callback<Settings.ProfileUpdateResponse> {
            override fun onResponse(
                call: Call<Settings.ProfileUpdateResponse>,
                response: Response<Settings.ProfileUpdateResponse>
            ) {
                binding.pb.hide()
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            MainApplication.authData = it.record
                            bitmap = null
                            showToast(it.message)
                            finish()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Settings.ProfileUpdateResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }


//    override fun onResume() {
//        super.onResume()
//
//        if (bitmap != null) {
//            binding.signView.show()
//            binding.signView.setImageBitmap(bitmap)
//            binding.checkbox.isChecked = true
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            if (bitmap != null) {
                binding.signView.show()
                binding.signView.setImageBitmap(bitmap)
                binding.checkbox.isChecked = true
            }
        }
    }

}
package com.emitrackon.emi_trackon.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.dashboard.DashboardActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.emitrackon.emi_trackon.databinding.ActivityLoginBinding
import com.emitrackon.emi_trackon.forgot_pass.ForgotPasswordActivity
import com.emitrackon.emi_trackon.utils.Constants
import com.emitrackon.emi_trackon.utils.SharedPref
import com.emitrackon.emi_trackon.utils.hide
import com.emitrackon.emi_trackon.utils.invisible
import com.emitrackon.emi_trackon.utils.isEmailValid
import com.emitrackon.emi_trackon.utils.isPhoneValid
import com.emitrackon.emi_trackon.utils.show
import com.emitrackon.emi_trackon.utils.showApiErrorToast
import com.emitrackon.emi_trackon.utils.showToast
import com.emitrackon.emi_trackon.utils.withNetwork
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    var selected = Constants.RETAILER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backImage.setOnClickListener {
            onBackPressed()
        }

        binding.btn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isEmpty() && pass.isEmpty()) {
                showToast("Please enter email and password.")
            } else if (email.isEmpty()) {
                showToast("Please enter email address.")
            } else if (email.contains("@") && !email.isEmailValid()) {
                showToast("Please enter a valid email")
            } else if (!email.contains("@") && !email.isPhoneValid()) {
                showToast("Please enter a valid phone number")
            } else if (pass.isEmpty()) {
                showToast("Please enter password.")
            } else {
                withNetwork { callLoginApi(email, pass) }
            }
        }

        binding.retailerTv.setOnClickListener {
            binding.retailerDivider.show()
            binding.distDivider.invisible()
            selected = Constants.RETAILER

        }

        binding.distribbutorTv.setOnClickListener {
            binding.retailerDivider.invisible()
            binding.distDivider.show()
            selected = Constants.DISTRIBUTOR
        }

        binding.forgotpassTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }


    }


    private fun callLoginApi(email: String, pass: String) {
        binding.pb.show()
        val role = if (selected == Constants.RETAILER) 3 else 2

        val req = Login.LoginRequest(email, pass, role)
        Log.d("RequestBody", Gson().toJson(req))
        val call = RetrofitInstance.apiService.loginApi(req)
        call.enqueue(object : Callback<Login.LoginResponse> {
            override fun onResponse(
                call: Call<Login.LoginResponse>, response: Response<Login.LoginResponse>
            ) {
                binding.pb.hide()

                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            SharedPref(this@LoginActivity).save(Constants.AUTH_KEY, it.access_token)
                            SharedPref(this@LoginActivity).save(Constants.IS_LOGGED_IN, true)
                            SharedPref(this@LoginActivity).save(Constants.ROLE, role)
                            SharedPref(this@LoginActivity).save(Constants.EMAIL, email)



                            if (selected == Constants.RETAILER) {
                                val intent = Intent(this@LoginActivity, DashBoardNewActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                startActivity(intent)
                            }
                            finish()
                        }
                    }
                    401 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            showToast(jObjError.getString("message"))
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Login.LoginResponse>, t: Throwable) {
                binding.pb.hide()
                showApiErrorToast()
            }

        })

    }
}
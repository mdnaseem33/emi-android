package com.emitrackon.emi_trackon.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.dashboard.DashboardActivity
import com.emitrackon.emi_trackon.dashboard.retailerModule.DashBoardNewActivity
import com.emitrackon.emi_trackon.login.LoginActivity
import com.emitrackon.emi_trackon.utils.Constants
import com.emitrackon.emi_trackon.utils.SharedPref

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(1000) {
            checkAuth()

        }
    }

    private fun checkAuth() {
        if (SharedPref(this@SplashActivity).getValueBoolean(
                Constants.IS_LOGGED_IN,
                false
            ) == true
        ) {
            if (SharedPref(this@SplashActivity).getValueInt(Constants.ROLE) == 3) {
                startActivity(Intent(this, DashBoardNewActivity::class.java))
            }else{
                startActivity(Intent(this, DashboardActivity::class.java))
            }

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
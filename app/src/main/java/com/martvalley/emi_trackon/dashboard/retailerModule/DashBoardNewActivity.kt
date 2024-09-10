package com.martvalley.emi_trackon.dashboard.retailerModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.databinding.ActivityDashboardNewBinding
import com.martvalley.emi_trackon.login.Auth
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardNewBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.viewActionBar)

        withNetwork { callAuthApi() }

        val navController = findNavController(R.id.frgContainerHome)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.userListFragment,
                R.id.statisticsFragment,
                R.id.retailerSettingFragment2
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.statisticsFragment || destination.id == R.id.retailerSettingFragment2 || destination.id == R.id.userListFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        // setUpButtonNavigation()

    }

    private fun callAuthApi() {
        val call = RetrofitInstance.apiService.getAuthApi()
        call.enqueue(object : Callback<Auth.AuthResponse> {
            override fun onResponse(
                call: Call<Auth.AuthResponse>, response: Response<Auth.AuthResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            SharedPref(this@DashBoardNewActivity).save(Constants.USERID, it.id)
                            SharedPref(this@DashBoardNewActivity).save(Constants.NAME, it.name)
                            MainApplication.authData = it
                        }
                    }
                    else -> {
                        showApiErrorToast()
                    }
                }
            }

            override fun onFailure(call: Call<Auth.AuthResponse>, t: Throwable) {
                showApiErrorToast()
            }

        })

    }
}
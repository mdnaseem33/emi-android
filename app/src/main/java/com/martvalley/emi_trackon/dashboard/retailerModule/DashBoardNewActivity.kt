package com.martvalley.emi_trackon.dashboard.retailerModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.martvalley.emi_trackon.MainApplication
import com.martvalley.emi_trackon.R
import com.martvalley.emi_trackon.api.RetrofitInstance
import com.martvalley.emi_trackon.dashboard.retailerModule.fragments.NotificationCountListener
import com.martvalley.emi_trackon.databinding.ActivityDashboardNewBinding
import com.martvalley.emi_trackon.login.Auth
import com.martvalley.emi_trackon.utils.Constants
import com.martvalley.emi_trackon.utils.SharedPref
import com.martvalley.emi_trackon.utils.showApiErrorToast
import com.martvalley.emi_trackon.utils.withNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class DashBoardNewActivity : AppCompatActivity(), NotificationCountListener {
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
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.userListFragment -> {
                    if(SharedPref(this).getValueInt(Constants.SUB_ROLE) == 2 || SharedPref(this).getValueInt(Constants.IS_RETAILER) == 2){
                        navController.navigate(R.id.people_retailer)
                    }else{
                        navController.navigate(R.id.userListFragment)
                    }
                    true
                }
                R.id.statisticsFragment -> {
                    navController.navigate(R.id.statisticsFragment)
                    true
                }
                R.id.retailerSettingFragment2 -> {
                    navController.navigate(R.id.retailerSettingFragment2)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationViewActivity::class.java))
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                supportActionBar?.show()
            } else {
                supportActionBar?.hide()
            }
        }
        binding.userNameTextView.text = SharedPref(this).getValueString(Constants.NAME)
        // Get current hour
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Determine greeting based on the current hour
        val greeting = when {
            currentHour in 5..11 -> "Good morning!"
            currentHour in 12..17 -> "Good afternoon!"
            else -> "Good evening!"
        }
        binding.staticText1.text = greeting
        // setUpButtonNavigation()

    }

    public fun callAuthApi() {
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
                            if (it.distributor_type != null && it.role==2){
                                if(SharedPref(this@DashBoardNewActivity).getValueInt(Constants.SUB_ROLE) == 0 ){
                                    SharedPref(this@DashBoardNewActivity).save(Constants.SUB_ROLE, it.distributor_type!!)
                                    if(it.distributor_type == 3){
                                        if(SharedPref(this@DashBoardNewActivity).getValueInt(Constants.IS_RETAILER) == 0){
                                            SharedPref(this@DashBoardNewActivity).save(Constants.IS_RETAILER, 2)
                                        }

                                    }
                                    recreate()
                                }
                                SharedPref(this@DashBoardNewActivity).save(Constants.SUB_ROLE, it.distributor_type!!)
                                if(it.distributor_type == 3){
                                    if(SharedPref(this@DashBoardNewActivity).getValueInt(Constants.IS_RETAILER) == null){
                                        SharedPref(this@DashBoardNewActivity).save(Constants.IS_RETAILER, 2)
                                    }

                                }


                            }
                            MainApplication.authData = it
                            binding.userNameTextView.text = it.name

                            if (it.image != null) {
                                val imageUrl = Constants.BASEURL + "storage/public/" +it.image
                                Glide.with(this@DashBoardNewActivity)
                                    .load(imageUrl)
                                    .into(binding.imageViewProfile);
                            }

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

    override fun onResume() {
        super.onResume()
        callAuthApi()
    }

    public fun changeNav(destinationId: Int) {
        val navController = findNavController(R.id.frgContainerHome)
        navController.navigate(destinationId)
    }

    override fun onNotificationCountUpdated(count: Int) {
        if(count > 0){
            binding.notificationCountTextView.visibility = View.VISIBLE
            binding.notificationCountTextView.text = count.toString()
        }else{
            binding.notificationCountTextView.visibility = View.GONE
        }

    }

}
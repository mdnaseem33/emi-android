package com.emitrackon.emi_trackon.dashboard

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.emitrackon.emi_trackon.MainApplication
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.api.RetrofitInstance
import com.emitrackon.emi_trackon.dashboard.home.distributor.DistributorFragment
import com.emitrackon.emi_trackon.dashboard.home.retailer.RetailerFragment
import com.emitrackon.emi_trackon.dashboard.people.retailer.RetailerListFragment
import com.emitrackon.emi_trackon.dashboard.people.user.UserFragment
import com.emitrackon.emi_trackon.dashboard.qr_code.QrFragment
import com.emitrackon.emi_trackon.dashboard.settings.distributor.DistributerSettingFragment
import com.emitrackon.emi_trackon.dashboard.settings.retail.RetailerSettingFragment
import com.emitrackon.emi_trackon.databinding.ActivityDashboardBinding
import com.emitrackon.emi_trackon.login.Auth
import com.emitrackon.emi_trackon.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DashboardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }
//    private val navController by lazy {
//        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this@DashboardActivity, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            //setupControls()
        }

        withNetwork { callAuthApi() }

        binding.toolbar.arrow.hide()
//        binding.bottomNav.setupWithNavController(navController)

//        if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 2) {
        binding.bottomNav.menu.removeItem(R.id.qr)
//        }

        setHome()


        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.toolbar.root.show()
                    binding.toolbar.filter.hide()
                    binding.toolbar.calender.hide()

                    binding.toolbar.text.text =
                        if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
//                            navController.navigate(R.id.home_retailer)
                            setFragment(RetailerFragment())
                            "Hi, Retailer"
                        } else {
//                            navController.navigate(R.id.home_distributor)
                            setFragment(DistributorFragment())
                            "Hi, Distributor"
                        }
                    false
                }
                R.id.people -> {
                    binding.toolbar.root.show()
                    binding.toolbar.filter.show()
                    binding.toolbar.calender.show()

                    binding.toolbar.text.text =

                        if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
                            setFragment(UserFragment())
//                            navController.navigate(R.id.people_user)
                            "User list"
                        } else {
                            setFragment(RetailerListFragment())
//                            navController.navigate(R.id.people_retailer)
                            "Retailer list"
                        }

                    true
                }
                R.id.qr -> {
                    binding.toolbar.filter.hide()
                    binding.toolbar.calender.hide()
                    binding.toolbar.root.hide()
                    setFragment(QrFragment())
                    true
//                    NavigationUI.onNavDestinationSelected(it, navController)
                }
                R.id.setting -> {
                    binding.toolbar.root.show()
                    binding.toolbar.filter.hide()
                    binding.toolbar.calender.hide()
                    binding.toolbar.text.text = "Settings"

                    if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
//                        navController.navigate(R.id.retailerSettingFragment)
                        setFragment(RetailerSettingFragment())
                    } else {
                        setFragment(DistributerSettingFragment())
//                        navController.navigate(R.id.distributerSettingFragment)
                    }
                    true
                }
                else -> false
            }

        }

        /*
     navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.home -> {
                        binding.toolbar.root.show()
                        binding.toolbar.filter.hide()
                        binding.toolbar.calender.hide()

                        binding.toolbar.text.text =
                            if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
                                controller.navigate(R.id.home_retailer)
                                "Hi, Retailer"
                            } else {
                                controller.navigate(R.id.home_distributor)
                                "Hi, Distributor"
                            }
                    }
                    R.id.people -> {
                        binding.toolbar.root.show()
                        binding.toolbar.filter.show()
                        binding.toolbar.calender.show()

                        binding.toolbar.text.text =
                            if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
                                controller.navigate(R.id.people_user)
                                "User list"
                            } else {
                                controller.navigate(R.id.people_retailer)
                                "Retailer list"
                            }
                    }
                    R.id.qr -> {
                        binding.toolbar.filter.hide()
                        binding.toolbar.calender.hide()
                        binding.toolbar.root.hide()
                    }
                    R.id.setting -> {
                        binding.toolbar.root.show()
                        binding.toolbar.filter.hide()
                        binding.toolbar.calender.hide()
                        binding.toolbar.text.text = "Settings"

                        if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
                            controller.navigate(R.id.retailerSettingFragment)
                        } else {
                            controller.navigate(R.id.distributerSettingFragment)
                        }
                    }
                }
            }*/

//        binding.bottomNav.setOnItemReselectedListener { }

    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@DashboardActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            134
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 134 && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setHome() {
        binding.toolbar.root.show()
        binding.toolbar.filter.hide()
        binding.toolbar.calender.hide()

        binding.toolbar.text.text =
            if (SharedPref(this@DashboardActivity).getValueInt(Constants.ROLE) == 3) {
                setFragment(RetailerFragment())
                "Hi, Retailer"
            } else {
                setFragment(DistributorFragment())
                "Hi, Distributor"
            }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    private fun callAuthApi() {
        val call = RetrofitInstance.apiService.getAuthApi()
        call.enqueue(object : Callback<Auth.AuthResponse> {
            override fun onResponse(
                call: Call<Auth.AuthResponse>, response: Response<Auth.AuthResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            SharedPref(this@DashboardActivity).save(Constants.USERID, it.id)
                            SharedPref(this@DashboardActivity).save(Constants.NAME, it.name)
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

//    override fun onBackPressed() {
//
//
//        finish()
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("Are you sure you want to close the app?")
//            .setMessage("Are you sure you want to close this activity?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, which -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
//            .addToBackStack(fragment.tag)
            .commit()
    }

}
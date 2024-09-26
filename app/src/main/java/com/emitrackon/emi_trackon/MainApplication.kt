package com.emitrackon.emi_trackon

import android.app.Application
import android.content.Context
import com.emitrackon.emi_trackon.login.Auth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
        var authData: Auth.AuthResponse ? = null

    }
}
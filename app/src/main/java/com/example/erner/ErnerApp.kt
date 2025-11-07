package com.example.erner

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory


class ErnerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val factory = if (isDebuggable)
            DebugAppCheckProviderFactory.getInstance()
        else
            PlayIntegrityAppCheckProviderFactory.getInstance()

        Firebase.appCheck.installAppCheckProviderFactory(factory)
    }
}
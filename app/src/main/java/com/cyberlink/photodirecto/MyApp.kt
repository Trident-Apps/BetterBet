package com.cyberlink.photodirecto

import android.app.Application
import com.onesignal.OneSignal

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(applicationContext.getString(R.string.onesignal_id))
    }

    companion object {
        lateinit var adID: String
    }
}
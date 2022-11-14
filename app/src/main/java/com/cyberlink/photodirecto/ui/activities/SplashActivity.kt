package com.cyberlink.photodirecto.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.MyApp
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            MyApp.adID = AdvertisingIdClient.getAdvertisingIdInfo(this@SplashActivity).id.toString()
            OneSignal.setExternalUserId(MyApp.adID)
            Log.d("customTage", "onesignal Init ${MyApp.adID}")
        }
        with(Intent(this, LoadingActivity::class.java)) {
            startActivity(this)
            this@SplashActivity.finish()
        }
    }
}
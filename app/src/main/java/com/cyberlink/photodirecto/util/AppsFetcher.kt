package com.cyberlink.photodirecto.util

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.cyberlink.photodirecto.R

class AppsFetcher {
    fun getApps(context: Context): MutableMap<String, Any>? {
        var data: MutableMap<String, Any>? = null
        AppsFlyerLib.getInstance()
            .init(context.getString(R.string.apps_dev_key), object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    data = p0
                }

                override fun onConversionDataFail(p0: String?) {
                    data = null
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}

                override fun onAttributionFailure(p0: String?) {}

            }, context)
        AppsFlyerLib.getInstance().start(context)
        return data
    }
}
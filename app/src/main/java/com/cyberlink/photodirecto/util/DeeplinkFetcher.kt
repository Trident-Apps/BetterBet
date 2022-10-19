package com.cyberlink.photodirecto.util

import android.content.Context
import com.facebook.applinks.AppLinkData

class DeeplinkFetcher() {

    fun getDeep(context: Context): String {
        var deepLink = "null"
        AppLinkData.fetchDeferredAppLinkData(context) {
            if (it != null) {
                deepLink = it.targetUri.toString()
            }
        }
        return deepLink
    }
}
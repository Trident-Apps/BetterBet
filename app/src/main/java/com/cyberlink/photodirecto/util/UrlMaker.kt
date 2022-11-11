package com.cyberlink.photodirecto.util

import android.content.Context
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.cyberlink.photodirecto.App
import com.cyberlink.photodirecto.R
import java.util.*

class UrlMaker {
    private var afId: String = ""
    private var source: String = ""
    private var _url = ""
    private val url get() = _url

    fun buildUrl(
        deeplink: String,
        data: MutableMap<String, Any>?,
        activity: Context?
    ): String {

        activity?.let { activity ->

            if (deeplink == "null") {
                source = data?.get(activity.getString(R.string.media_source)).toString()
                afId = AppsFlyerLib.getInstance().getAppsFlyerUID(activity).toString()
            } else {
                source = "deeplink"
                afId = "null"
            }

            _url = activity.getString(R.string.base_url).toUri().buildUpon().apply {
                appendQueryParameter(
                    activity.getString(R.string.secure_get_parametr),
                    activity.getString(R.string.secure_key)
                )
                appendQueryParameter(
                    activity.getString(R.string.dev_tmz_key),
                    TimeZone.getDefault().id
                )
                appendQueryParameter(activity.getString(R.string.gadid_key), App.adID)
                appendQueryParameter(activity.getString(R.string.deeplink_key), deeplink)
                appendQueryParameter(activity.getString(R.string.source_key), source)
                appendQueryParameter(
                    activity.getString(R.string.af_id_key),
                    afId
                )
                appendQueryParameter(
                    activity.getString(R.string.adset_id_key),
                    data?.get(activity.getString(R.string.adset_id)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.campaign_id_key),
                    data?.get(activity.getString(R.string.campaign_id)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.app_campaign_key),
                    data?.get(activity.getString(R.string.campaign)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.adset_key),
                    data?.get(activity.getString(R.string.adset)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.adgroup_key),
                    data?.get(activity.getString(R.string.adgroup)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.orig_cost_key),
                    data?.get(activity.getString(R.string.orig_cost)).toString()
                )
                appendQueryParameter(
                    activity.getString(R.string.af_siteid_key),
                    data?.get(activity.getString(R.string.af_siteid)).toString()
                )

            }.toString()
        }
        return url
    }
}
package com.cyberlink.photodirecto.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.cyberlink.photodirecto.App
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.ui.intents.DeepLinkIntent
import com.cyberlink.photodirecto.ui.states.DeepLinkState
import com.cyberlink.photodirecto.util.CustomDatabase
import com.cyberlink.photodirecto.util.UrlBuilder
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


class MyViewModel(val app: App) : ViewModel() {

    private var _data: MutableMap<String, Any>? = null
    private val data get() = _data
    private var _deep = "null"
    private val deepLink get() = _deep
    private val builder = UrlBuilder()
    private var _url = ""
    private val url get() = _url
    private val firebase = CustomDatabase()

    val deepIntent = Channel<DeepLinkIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<DeepLinkState>(DeepLinkState.Idle)
    val state: StateFlow<DeepLinkState> get() = _state

    fun handleIntent(activity: Context) {
        viewModelScope.launch {
            deepIntent.consumeAsFlow().collect() {
                when (it) {
                    is DeepLinkIntent.FetchDeepLink -> fetchDeep(activity)
                }
            }
        }
    }

    private fun fetchDeep(context: Context) {
        Log.d("customTagVM", "fetch deep")
        viewModelScope.launch {
            _state.value = DeepLinkState.Loading
            _state.value = try {
                DeepLinkState.DeepLink(deepLinkRequest(context))
            } catch (e: Exception) {
                DeepLinkState.Error(e.localizedMessage)
            }
        }
    }

    private suspend fun deepLinkRequest(activity: Context): String {
        AppLinkData.fetchDeferredAppLinkData(activity) {
            _deep = it?.targetUri.toString()
            Log.d("customTagVM", "deep is $_deep")
        }
//        _url = if (deepLink == "null") {
//            builder.buildUrl("null", appsDataRequest(activity), activity)
//        } else {
//            sendTag(deepLink, null)
//            builder.buildUrl(deepLink, null, activity)
//        }
        when (deepLink) {
            "null" -> {
                _url = builder.buildUrl("null", appsDataRequest(activity), activity)
            }
            else -> {
                sendTag(deepLink, null)
                _url = builder.buildUrl(deepLink, null, activity)
            }

        }
        return url
    }

    private suspend fun appsDataRequest(activity: Context): MutableMap<String, Any>? {
        val savedApps = firebase.getData(App.adID)
        if (savedApps == null) {
            AppsFlyerLib.getInstance()
                .init(activity.getString(R.string.apps_dev_key), object : AppsFlyerConversionListener {
                    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                        _data = p0
                        sendTag("null", _data)
                        Log.d("customTagVM", "the success data is - ${_data.toString()}")

                    }

                    override fun onConversionDataFail(p0: String?) {
                        _data = null
                    }

                    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAttributionFailure(p0: String?) {
                        TODO("Not yet implemented")
                    }

                }, activity)
            AppsFlyerLib.getInstance().start(activity)
        }
        Log.d("customTagVM", "the return _data is - ${_data.toString()}")
        Log.d("customTagVM", "the return data is - ${data.toString()}")
        return data
    }

    fun sendTag(deepLink: String, data: MutableMap<String, Any>?) {
        val campaign = data?.get("campaign").toString()

        if (campaign == "null" && deepLink == "null") {
            OneSignal.sendTag("key2", "organic")
        } else if (deepLink != "null") {
            OneSignal.sendTag("key2", deepLink.replace("myapp://", "").substringBefore("/"))
        } else if (campaign != "null") {
            OneSignal.sendTag("key2", campaign.substringBefore("_"))
        }
    }
}
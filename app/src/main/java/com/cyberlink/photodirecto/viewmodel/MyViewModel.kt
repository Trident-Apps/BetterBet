package com.cyberlink.photodirecto.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.ui.intents.AppsIntent
import com.cyberlink.photodirecto.ui.intents.DeepLinkIntent
import com.cyberlink.photodirecto.ui.states.AppsState
import com.cyberlink.photodirecto.ui.states.DeepLinkState
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MyViewModel(app: Application) : AndroidViewModel(app) {

    private var _data: MutableMap<String, Any>? = null
    private val data get() = _data
    private var _deep: String? = null
    private val deep get() = _deep
    val deepIntent = Channel<DeepLinkIntent>(Channel.UNLIMITED)
    val dataIntent = Channel<AppsIntent>(Channel.UNLIMITED)
    private val _dataState = MutableStateFlow<AppsState>(AppsState.Idle)
    private val _state = MutableStateFlow<DeepLinkState>(DeepLinkState.Idle)
    val state: StateFlow<DeepLinkState> get() = _state
    val dataState: StateFlow<AppsState> get() = _dataState

    fun handleDeppIntent(activity: Context) {
        viewModelScope.launch {
            deepIntent.consumeAsFlow().collect {
                when (it) {
                    is DeepLinkIntent.FetchDeepLink -> fetchDeep(activity)
                }
            }
        }
    }

    fun handleDataIntent(activity: Context) {
        viewModelScope.launch {
            dataIntent.consumeAsFlow().collect {
                when (it) {
                    is AppsIntent.FetchAppsData -> fetchData(activity)
                }
            }
        }
    }

    private fun fetchDeep(activity: Context) {
        Log.d("customTagVM", "fetch deep")
        viewModelScope.launch {
            _state.value = DeepLinkState.Loading
            deepFlow(activity).collect {
                _deep = it
                _state.value = DeepLinkState.DeepLink(deep)
                Log.d("customTagVM", "$it deep collected")
            }
        }
    }

    private fun fetchData(activity: Context) {
        viewModelScope.launch {
            _dataState.value = AppsState.Loading
            dataFlow(activity).collect {
                _data = it
                _dataState.value = AppsState.Data(data)
                Log.d("customTagVM", "$it data collected")
            }
        }
    }


    private fun deepFlow(activity: Context): Flow<String?> = callbackFlow {
        AppLinkData.fetchDeferredAppLinkData(activity) {
            trySend(it?.targetUri.toString())
        }
        awaitClose()
    }

    private fun dataFlow(activity: Context): Flow<MutableMap<String, Any>?> = callbackFlow {
        AppsFlyerLib.getInstance().init(
            activity.getString(R.string.apps_dev_key),
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    trySend(p0)
                    Log.d("customTagVM", "onConversionDataSuccess")
                }

                override fun onConversionDataFail(p0: String?) {
                    trySend(null)
                    Log.d("customTagVM", "onConversionDataFail")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    TODO("Not yet implemented")
                }

                override fun onAttributionFailure(p0: String?) {
                    TODO("Not yet implemented")
                }
            }, activity
        )
        AppsFlyerLib.getInstance().start(activity)
        awaitClose {
            cancel()
        }
    }

    fun makeTag(deepLink: String, data: MutableMap<String, Any>?) {
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
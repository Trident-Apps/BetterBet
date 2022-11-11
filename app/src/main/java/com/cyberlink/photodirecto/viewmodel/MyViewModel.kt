package com.cyberlink.photodirecto.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.cyberlink.photodirecto.App
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.ui.intents.AppsIntent
import com.cyberlink.photodirecto.ui.intents.DeepLinkIntent
import com.cyberlink.photodirecto.ui.states.AppsState
import com.cyberlink.photodirecto.ui.states.DeepLinkState
import com.cyberlink.photodirecto.util.CustomDatabase
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MyViewModel(val app: App, private val activity: Activity) : AndroidViewModel(app) {

    private var _data: MutableMap<String, Any>? = null
    private val data get() = _data
    private var _deep: String? = null
    private val deep get() = _deep
    private var _url = ""
    private val url get() = _url
    private val firebase = CustomDatabase()

    val deepIntent = Channel<DeepLinkIntent>(Channel.UNLIMITED)
    val dataIntent = Channel<AppsIntent>(Channel.UNLIMITED)
    private val _dataState = MutableStateFlow<AppsState>(AppsState.Idle)
    private val _state = MutableStateFlow<DeepLinkState>(DeepLinkState.Idle)
    val state: StateFlow<DeepLinkState> get() = _state
    val dataState: StateFlow<AppsState> get() = _dataState


    fun handleDeppIntent() {
        viewModelScope.launch {
            deepIntent.consumeAsFlow().collect() {
                when (it) {
                    is DeepLinkIntent.FetchDeepLink -> fetchDeep()
                }
            }
        }
    }

    fun handleDataIntent() {
        viewModelScope.launch {
            dataIntent.consumeAsFlow().collect {
                when (it) {
                    is AppsIntent.FetchAppsData -> fetchData()
                }
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            _dataState.value = AppsState.Loading
            _dataState.value = try {
                dataFlow.collect {
                    _data = it
                }
                AppsState.Data(data)
            } catch (e: Exception) {
                AppsState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchDeep() {
        Log.d("customTagVM", "fetch deep")
        viewModelScope.launch {
            _state.value = DeepLinkState.Loading
            _state.value = try {
                deepFlow.collect {
                    _deep = it
                }
                DeepLinkState.DeepLink(deep)
            } catch (e: Exception) {
                DeepLinkState.Error(e.localizedMessage)
            }
        }
    }

    private val deepFlow: Flow<String?> = callbackFlow {
        AppLinkData.fetchDeferredAppLinkData(activity) {
            trySend(it?.targetUri.toString())
            makeTag(it?.targetUri.toString(), null)
        }
        awaitClose()
    }

    private val dataFlow: Flow<MutableMap<String, Any>?> = callbackFlow {
        AppsFlyerLib.getInstance().init(
            activity.getString(R.string.apps_dev_key),
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d("aaaaaaa", "onConversionDataSuccess")
                    trySend(p0)
                    makeTag("null", p0)
                }

                override fun onConversionDataFail(p0: String?) {
                    trySend(null)
                    Log.d("aaaaaaa", "onConversionDataFail")
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
        awaitClose()
    }

//    private suspend fun deepLinkRequest(activity: Context): String {
//        AppLinkData.fetchDeferredAppLinkData(activity) {
//            _deep = "null"
//            Log.d("customTagVM", "deep is $_deep")
//        }
//
//        Log.d("customTagVM", "deep after method $_deep")
//        when (deepLink) {
//            "null" -> {
//                _url = builder.buildUrl("null", appsDataRequest(activity), activity)
//                Log.d("customTagVM", "started apps")
//            }
//            else -> {
//                sendTag(deepLink, null)
//                _url = builder.buildUrl(deepLink, null, activity)
//                Log.d("customTagVM", "created url from deep")
//            }
//
//        }
//        return url
//    }

//    private suspend fun appsDataRequest(activity: Context): MutableMap<String, Any>? {
//        val savedApps = firebase.getData(App.adID)
//        if (savedApps == null) {
//            AppsFlyerLib.getInstance()
//                .init(
//                    activity.getString(R.string.apps_dev_key),
//                    object : AppsFlyerConversionListener {
//                        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                            val mockData: MutableMap<String, Any>? = mutableMapOf(
//                                "af_status" to "Non-organic",
//                                "media_source" to "testSource",
//                                "campaign" to "test1_test2_test3_test4_test5",
//                                "adset" to "testAdset",
//                                "adset_id" to "testAdsetId",
//                                "campaign_id" to "testCampaignId",
//                                "orig_cost" to "1.22",
//                                "af_site_id" to "testSiteID",
//                                "adgroup" to "testAdgroup"
//                            )
//                            _data = mockData
//                            makeTag("null", _data)
//                            Log.d("customTagVM", "the success data is - ${_data.toString()}")
//
//                        }
//
//                        override fun onConversionDataFail(p0: String?) {
//                            _data = null
//                            Log.d("customTagVM", "onConversionDataFail")
//                        }
//
//                        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
//                            TODO("Not yet implemented")
//                        }
//
//                        override fun onAttributionFailure(p0: String?) {
//                            TODO("Not yet implemented")
//                        }
//
//                    },
//                    activity
//                )
//            AppsFlyerLib.getInstance().start(activity)
//        }
//        Log.d("customTagVM", "the return _data is - ${_data.toString()}")
//        Log.d("customTagVM", "the return data is - ${data.toString()}")
//        return data
//    }


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
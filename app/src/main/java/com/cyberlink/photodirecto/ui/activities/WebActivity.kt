package com.cyberlink.photodirecto.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.cyberlink.photodirecto.App
import com.cyberlink.photodirecto.R
import com.cyberlink.photodirecto.databinding.WebViewActivityBinding
import com.cyberlink.photodirecto.ui.activities.cloak.CloakActivity
import com.cyberlink.photodirecto.util.CustomDatabase

class WebActivity : AppCompatActivity() {

    private var _binding: WebViewActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var webView: WebView
    private var isRedirected: Boolean = true
    private var message: ValueCallback<Array<Uri?>>? = null
    private lateinit var url: String
    private val firebase = CustomDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("customWeb", "init super")
        _binding = WebViewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("customWeb", "init view")

        url = intent.getStringExtra("url") ?: "url not passed"
        Log.d("customWeb", url)
        webView = binding.webView
        webView.loadUrl(url)
        webView.webViewClient = LocalClient()
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.userAgentString =
            System.getProperty(this@WebActivity.getString(R.string.user_agent))
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                message = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWeb = WebView(this@WebActivity)
                newWeb.webChromeClient = this
                newWeb.settings.javaScriptEnabled = true
                newWeb.settings.javaScriptCanOpenWindowsAutomatically = true
                newWeb.settings.domStorageEnabled = true
                newWeb.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWeb
                resultMsg.sendToTarget()
                newWeb.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view?.loadUrl(url ?: "")
                        isRedirected = true
                        return true
                    }
                }
                return true
            }
        }
    }

    private fun selectImageIfNeed() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = this@WebActivity.getString(R.string.intent_type)
        startActivityForResult(
            Intent.createChooser(intent, this@WebActivity.getString(R.string.chooser_title)), 1
        )
    }

    private inner class LocalClient() : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            isRedirected = false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            CookieManager.getInstance().flush()
            val shortUrl = this@WebActivity.getString(R.string.base_url_short)
            if (!isRedirected) {
                if (url == shortUrl) {
                    with((Intent(this@WebActivity, CloakActivity::class.java))) {
                        startActivity(this)
                        this@WebActivity.finish()
                    }
                } else {
//                    val savedUrl = firebase.getData(App.adID).toString()
//                    if (savedUrl == "null") {
                    firebase.saveUser(App.adID, url!!, true)
//                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
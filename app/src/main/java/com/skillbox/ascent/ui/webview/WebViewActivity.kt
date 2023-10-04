package com.skillbox.ascent.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.skillbox.ascent.R

@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_webview)
        val progressBar = findViewById<ProgressBar>(R.id.progress)
        val loadText = findViewById<TextView>(R.id.loading)
        progressBar.max = 100
        webView = findViewById(R.id.myWebView)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                    loadText.isVisible = false
                } else {
                    loadText.isVisible = true
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }
        }

        // включаем поддержку JavaScript
        webView.settings.javaScriptEnabled = true
        val data = intent.data
        webView.loadUrl(data.toString())
    }
}
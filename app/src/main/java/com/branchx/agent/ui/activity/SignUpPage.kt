package com.branchx.agent.ui.activity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.branchx.agent.R

class SignUpPage : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        webView =  findViewById(R.id.webView);
        webView?.settings?.javaScriptEnabled = true
        webView?.loadUrl("https://partner.branchx.in/Login/Registration")

    }

}
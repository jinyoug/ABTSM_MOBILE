package com.skcc.abtsm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SendActivity extends AppCompatActivity {

        private WebView mWebView;
        private WebSettings mWebSettings;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_send);

            mWebView = (WebView)findViewById(R.id.webview_login);
            mWebView.setWebViewClient(new WebViewClient());
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);


            mWebView.loadUrl("http://abtsm-fe.paas.sk.com/chatbot/");
        }
    }
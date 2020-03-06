package app.amazon.integratedapp;

import android.webkit.WebView;

import android.webkit.WebViewClient;
public class StatisticsActivity  extends WebViewClient  {


    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return false;
    }

}



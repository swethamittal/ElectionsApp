package app.amazon.integratedapp;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RulesActivity extends WebViewClient{


    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return false;
    }
}


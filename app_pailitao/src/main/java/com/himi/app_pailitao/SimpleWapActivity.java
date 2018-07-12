
package com.himi.app_pailitao;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

public class SimpleWapActivity extends Activity implements View.OnClickListener {
    String mTitle;
    String mUrl;

    private WebView mWebView;
    private ProgressBar mProgressWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");

        initViews();
        afterInitViews();
    }

    protected void initViews() {
        setContentView(R.layout.activity_simple_wap);

        findViewById(R.id.title_bt_back).setOnClickListener(this);

        mWebView = (WebView) findViewById(R.id.webview);

        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadUrl(mUrl);

        String u = "http://www.pailitao.com/search?q=&imgfile=&tfsid=TB1dQQBBkKWBuNjy1zjXXcOypXa&app=imgsearch";
        mWebView.loadUrl(u);
        mProgressWeb = (ProgressBar) findViewById(R.id.progress_web);
    }

    protected void afterInitViews() {
        if (!TextUtils.isEmpty(mTitle)) {
            ((TextView) findViewById(R.id.title_name)).setText(mTitle);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.title_bt_back) {
            finish();
        }
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressWeb.setVisibility(View.GONE);
            } else {
                mProgressWeb.setVisibility(View.VISIBLE);
                mProgressWeb.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class WebViewClient extends android.webkit.WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            Log.d("123", "CookieStr = " + CookieStr);

            // 获取页面内容
            view.loadUrl("javascript:window.java_obj.showSource("
                    + "document.getElementsByTagName('html')[0].innerHTML);");

            // 获取解析<meta name="share-description" content="获取到的值">
//            view.loadUrl("javascript:window.java_obj.showDescription("
//                    + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
//                    + ");");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // https://blog.csdn.net/z82367825/article/details/52187921
    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
//            System.out.println("====>html=" + html);
            int start = html.indexOf("g_page_config = ");
            int end = html.indexOf(";", start);
            Log.d("123", "start = " + start + ", end = " + end);
                    String g_page_config = html.substring(start + 16, end);
            Log.d("123", "g_page_config = " + g_page_config);
        }

        @JavascriptInterface
        public void showDescription(String str) {
            System.out.println("====>html=" + str);
        }
    }

}

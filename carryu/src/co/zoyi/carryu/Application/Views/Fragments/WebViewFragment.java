package co.zoyi.carryu.Application.Views.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class WebViewFragment extends CUFragment implements Refreshable {
    private WebView webView;
    private String url;
    private WebViewStatusChangeListener webViewStatusChangeListener;

    public static interface WebViewStatusChangeListener {
        public void onPageStarted(WebView webView);
        public void onPageFinished(WebView webView);
    }

    public void setWebViewStatusChangeListener(WebViewStatusChangeListener webViewStatusChangeListener) {
        this.webViewStatusChangeListener = webViewStatusChangeListener;
    }

    private void fireOnPageFinishedListener() {
        if (webViewStatusChangeListener != null) {
            webViewStatusChangeListener.onPageFinished(webView);
        }
    }

    private void fireOnPageStartedListener() {
        if (webViewStatusChangeListener != null) {
            webViewStatusChangeListener.onPageStarted(webView);
        }
    }

    private void initializeWebView() {
        this.webView = (WebView) getView().findViewById(R.id.web_view);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setUserAgentString("CarryU");
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                fireOnPageStartedListener();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                fireOnPageFinishedListener();
            }
        });

        if (this.url != null) {
            this.webView.loadUrl(this.url);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void clearHistory() {
        webView.clearHistory();
    }

    public void loadUrl(String url) {
        CUUtil.log(this, "Load URL: " + url);
        this.url = url;
        webView.loadUrl(url);
        fireOnPageStartedListener();
    }

    @Override
    public void refresh() {
        if (this.url == null) {
            EventBus.getDefault().post(new NeedRefreshFragmentEvent(this));
        }
    }

    public boolean goBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}

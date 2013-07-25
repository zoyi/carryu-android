package co.zoyi.carryu.Application.Views.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import co.zoyi.carryu.R;

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

    private void initializeWebView() {
        this.webView = (WebView) getView().findViewById(R.id.web_view);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setUserAgentString("CarryU");
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webViewStatusChangeListener != null) {
                    webViewStatusChangeListener.onPageFinished(view);
                }
            }
        });

        if (this.url != null) {
            this.webView.loadUrl(this.url);
        }
    }

    public void clearHistory() {
        webView.clearHistory();
    }

    public void loadUrl(String url) {
        this.url = url;
        if (webView != null) {
            if (webViewStatusChangeListener != null) {
                webViewStatusChangeListener.onPageStarted(webView);
            }
            webView.loadUrl(url);
        }
    }

    @Override
    public void refresh() {
        if (webView != null) {
            if (this.url != null && webView.getUrl() == null) {
                loadUrl(this.url);
            } else {
                if (webViewStatusChangeListener != null) {
                    webViewStatusChangeListener.onPageStarted(webView);
                }
                webView.reload();
            }
        }
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}

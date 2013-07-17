package co.zoyi.carryu.Application.Views.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import co.zoyi.carryu.R;

public class SummonerDetailActivity extends CUActivity {
    private WebView webView;
    private String summonerName;

    private View.OnClickListener onBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private View.OnClickListener onRefreshButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            webView.reload();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summoner_detail_activity);

        summonerName = getIntent().getStringExtra("SUMMONER_NAME");

        ((TextView)findViewById(R.id.tv_title)).setText(summonerName);

        findViewById(R.id.back).setOnClickListener(onBackButtonClickListener);
        findViewById(R.id.refresh).setOnClickListener(onRefreshButtonClickListener);

        initializeWebView();
    }

    private void initializeWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showWaitingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideWaitingDialog();
            }
        });

        webView.loadUrl(String.format("http://carryu.co/summoners/%s?region=%s&query=%s", "AllenJee", "kr", "AllenJee"));
    }
}

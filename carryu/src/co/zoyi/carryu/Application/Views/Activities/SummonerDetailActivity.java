package co.zoyi.carryu.Application.Views.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;

import java.util.HashMap;
import java.util.Map;

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
            refresh();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summoner_detail_activity);

        summonerName = getIntent().getStringExtra("SUMMONER_NAME");

        ((TextView)findViewById(R.id.title)).setText(summonerName);

        findViewById(R.id.back).setOnClickListener(onBackButtonClickListener);
        findViewById(R.id.refresh).setOnClickListener(onRefreshButtonClickListener);

        initializeWebView();

        refresh();
    }

    private void initializeWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ((TextView)findViewById(R.id.title)).setText(getString(R.string.loading));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ((TextView)findViewById(R.id.title)).setText(summonerName);
            }
        });
    }

    public void refresh() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "CarryU");
        ((TextView)findViewById(R.id.title)).setText(getString(R.string.loading));
        webView.loadUrl(String.format("http://%s.carryu.co/summoners/%s", Registry.getChatService().getChatServerInfo().getRegion(), summonerName), headers);
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        if (event.getStatus() == ChatService.Status.OUT_OF_GAME) {
            finish();
        } else if (event.getStatus() == ChatService.Status.IN_GAME) {
            ActivityDelegate.openInGameActivity(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            finish();
        }
    }
}

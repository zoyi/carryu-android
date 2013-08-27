package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.Application.Views.Fragments.WebViewFragment;
import co.zoyi.carryu.R;

public class SummonerDetailActivity extends CUActivity implements Refreshable {
    public static String SUMMONER_NAME_INTENT_KEY = "summoner_name";

    private WebViewFragment webViewFragment;
    private String summonerName;

    private View.OnClickListener onBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private WebViewFragment.WebViewStatusChangeListener webViewStatusChangeListener = new WebViewFragment.WebViewStatusChangeListener() {
        @Override
        public void onPageStarted(WebView webView) {
            setTitle(R.string.loading);
        }

        @Override
        public void onPageFinished(WebView webView) {
            setTitle(summonerName);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summoner_detail_activity);

        summonerName = getIntent().getStringExtra(SUMMONER_NAME_INTENT_KEY);

        findViewById(R.id.back).setOnClickListener(onBackButtonClickListener);

        webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.web_view_fragment);
        webViewFragment.setWebViewStatusChangeListener(webViewStatusChangeListener);

        refresh();
    }

    @Override
    public void refresh() {
        webViewFragment.loadUrl(CURouter.getSummonerDetailURL(summonerName));
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
        if (this.webViewFragment.goBack() == false) {
            finish();
        }
    }
}

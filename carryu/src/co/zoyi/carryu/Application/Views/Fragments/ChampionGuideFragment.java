package co.zoyi.carryu.Application.Views.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.WebViewFragmentStatusChangedEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Activities.InGameActivity;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

import java.util.HashMap;
import java.util.Map;

public class ChampionGuideFragment extends TabContentFragment {
    private int championId = -1;
    private WebView webView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.webView = (WebView) view.findViewById(R.id.web_view);
        initializeWebView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.champion_guide_fragment, container, false);
    }

    @Override
    public void refresh() {
        CUUtil.log(this, String.format("Load: http://%s.carryu.co/champions/%d/guides", Registry.getChatService().getChatServerInfo().getRegion(), championId));
        if (championId != -1) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", "CarryU");
            EventBus.getDefault().post(new WebViewFragmentStatusChangedEvent(ChampionGuideFragment.this, WebViewFragmentStatusChangedEvent.Status.STARTED));
            webView.loadUrl(String.format("http://%s.carryu.co/champions/%d/guides", Registry.getChatService().getChatServerInfo().getRegion(), championId), headers);
        }
    }

    public void setChampionId(int championId) {
//        CUUtil.log(this, "set champion id: " + String.valueOf(championId));
        this.championId = championId;
    }

    private void initializeWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                EventBus.getDefault().post(new WebViewFragmentStatusChangedEvent(ChampionGuideFragment.this, WebViewFragmentStatusChangedEvent.Status.FINISHED));
            }
        });
    }

    public boolean onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return false;
    }
}

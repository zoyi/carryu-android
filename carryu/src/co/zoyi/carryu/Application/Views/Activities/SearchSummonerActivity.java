package co.zoyi.carryu.Application.Views.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;

import java.util.HashMap;
import java.util.Map;

public class SearchSummonerActivity extends Activity {
    private WebView webView;

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            search();
        }
    };

    private void search() {
        String summonerName = EditText.class.cast(findViewById(R.id.summoner_name)).getText().toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "CarryU");
        webView.loadUrl(String.format("http://%s.carryu.co/summoners/%s", Registry.getChatService().getChatServerInfo().getRegion(), summonerName), headers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        findViewById(R.id.search).setOnClickListener(searchClickListener);

        initializeWebView();
    }

    private void initializeWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showWaitingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                hideWaitingDialog();
            }
        });
    }
}

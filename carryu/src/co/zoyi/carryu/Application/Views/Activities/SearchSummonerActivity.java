package co.zoyi.carryu.Application.Views.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Fragments.WebViewFragment;
import co.zoyi.carryu.R;

public class SearchSummonerActivity extends CUActivity {
    private String summonerName;
    private WebViewFragment webViewFragment;

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            search();
        }
    };

    private WebViewFragment.WebViewStatusChangeListener webViewStatusChangeListener = new WebViewFragment.WebViewStatusChangeListener() {
        @Override
        public void onPageStarted(final WebView webView) {
            showProgressDialog(getString(R.string.loading)).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    webView.stopLoading();
                    hideProgressDialog();
                }
            });
        }

        @Override
        public void onPageFinished(WebView webView) {
            hideProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        findViewById(R.id.search).setOnClickListener(searchClickListener);

        webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.web_view_fragment);
        webViewFragment.setWebViewStatusChangeListener(webViewStatusChangeListener);

        EditText.class.cast(findViewById(R.id.summoner_name)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webViewFragment.goBack() == false) {
            finish();
        }
    }

    private void search() {
        summonerName = EditText.class.cast(findViewById(R.id.summoner_name)).getText().toString();
        webViewFragment.clearHistory();
        webViewFragment.loadUrl(String.format("http://%s.carryu.co/summoners/%s", Registry.getChatService().getChatServerInfo().getRegion(), summonerName));
    }
}

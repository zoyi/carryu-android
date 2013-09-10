package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerStatus;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
import co.zoyi.carryu.R;
import com.google.gson.Gson;
import com.sbstrm.appirater.Appirater;

import java.util.Map;


public class LoginSelectActivity extends CUActivity {
    AlertDialog alertDialog = null;

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityDelegate.openSearchSummonerActivity(LoginSelectActivity.this);
        }
    };

    private View.OnClickListener sampleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityDelegate.openSampleInGameActivity(LoginSelectActivity.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appirater.appLaunched(this);

        setContentView(R.layout.login_select_activity);

        findViewById(R.id.search).setOnClickListener(searchClickListener);
        Button.class.cast(findViewById(R.id.sample)).setOnClickListener(sampleClickListener);
        fetchServerStatus();
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return false;
    }

    private void showServerStatusUnavailable(ServerStatus serverStatus) {
        String show_message = "";
        for (Map<String, String> message : serverStatus.getErrors()) {
            show_message = show_message.concat("\n" + message.get("message"));
        }

        if (alertDialog == null)
            alertDialog = new AlertDialog(this, show_message);
        if (!alertDialog.isShowing())
            alertDialog.show();
    }

    private void fetchServerStatus() {
        HttpRequestDelegate.fetchServerStatus(new DataCallback<ServerStatus>() {
            @Override
            public void onError(Throwable throwable, String s) {
                super.onError(throwable, s);
                Gson gson = new Gson();
                ServerStatus serverStatus = gson.fromJson(s, ServerStatus.class);
                showServerStatusUnavailable(serverStatus);
            }
        });
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        super.processChatStatus(status);
    }

    public void onLoginWithRiotAccountButtonClick(View loginButton) {
        ActivityDelegate.openLoginActivity(LoginSelectActivity.this);
    }

    public void onLoginWithSummonerIdButtonClick(View loginButton) {
        ActivityDelegate.openSummonerIdLoginActivity(LoginSelectActivity.this);
    }

    public void onSampleGameButtonClick(View loginButton) {
        ActivityDelegate.openSampleInGameActivity(LoginSelectActivity.this);
    }
}
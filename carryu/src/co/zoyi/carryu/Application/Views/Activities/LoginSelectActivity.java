package co.zoyi.carryu.Application.Views.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.R;


public class LoginSelectActivity extends CUActivity {
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

        setContentView(R.layout.login_select_activity);

        findViewById(R.id.search).setOnClickListener(searchClickListener);
        Button.class.cast(findViewById(R.id.sample)).setOnClickListener(sampleClickListener);
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return false;
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
package co.zoyi.carryu.Application.Views.Activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import co.zoyi.Chat.Datas.ChatServerInfo;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Datas.Serializers.JSONSerializer;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.*;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class LoginActivity extends CUActivity {
    public static String CONNECTION_CLOSED_INTENT_KEY = "connection_closed";

    private String SERVER_INFO_PREFERENCE_KEY = "server_info_pref";
    private String SERVER_INFO_JSON_KEY = "server_info";
    private String DEFAULT_SERVER_INFO_JSON_FILE = "default_server_info.json";

    private String NOTICE_PREFERENCE_KEY = "notice_pref";
    private String NOTICE_PASSWORD_KEY = "notice_password_key";

    private ServerList serverList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.login_activity);

         if (ActivityDelegate.hasIntentExtra(this, CONNECTION_CLOSED_INTENT_KEY)) {
             EventBus.getDefault().post(new ConnectionClosedErrorEvent());
         }
         initializeViews();
         restoreViewPreferences();
         restoreServerInfo();
         fetchServerInfo();
    }

    private void showPasswordNoticeDialog() {
        AlertDialog alertDialog = new AlertDialog(this, getString(R.string.password_notice));
        alertDialog.show();
    }

    private void initializeViews() {
        ActivityDelegate.addUnderline(TextView.class.cast(findViewById(R.id.login_message)));

        TextView.class.cast(findViewById(R.id.login_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordNoticeDialog();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(NOTICE_PREFERENCE_KEY, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(NOTICE_PASSWORD_KEY, false)) {
            showPasswordNoticeDialog();
            sharedPreferences.edit().putBoolean(NOTICE_PASSWORD_KEY, true).commit();
        }

    }

    private void fetchServerInfo() {
        HttpRequestDelegate.fetchServerInfo(this, new DataCallback<ServerList>() {
            @Override
            public void onSuccess(ServerList serverList) {
                super.onSuccess(serverList);
                LoginActivity.this.serverList = serverList;
                storeServerInfo();
            }
        });
    }

    private void storeViewPreferences() {
        new ViewPreferenceManager.Storage(this)
            .save(R.id.user_id, "user_id")
            .save(R.id.user_password, "user_password");
    }

    private void restoreViewPreferences() {
        new ViewPreferenceManager.Loader(this)
            .load(R.id.user_id, "user_id")
            .load(R.id.user_password, "user_password");
    }

    private void storeServerInfo() {
        String serverListJsonString = JSONSerializer.getGsonInstance().toJson(this.serverList);
        SharedPreferences.Editor editor = getSharedPreferences(SERVER_INFO_PREFERENCE_KEY, MODE_PRIVATE).edit();
        editor.putString(SERVER_INFO_JSON_KEY, serverListJsonString);
        editor.commit();
    }

    private void restoreServerInfo() {
        SharedPreferences sharePreference = getSharedPreferences(SERVER_INFO_PREFERENCE_KEY, MODE_PRIVATE);
        if (sharePreference.contains(SERVER_INFO_JSON_KEY) == false) {
            this.serverList = Registry.getServerListJSONSerializer().toObject(AssetReader.readString(this, DEFAULT_SERVER_INFO_JSON_FILE), ServerList.class);
        } else {
            this.serverList = Registry.getServerListJSONSerializer().toObject(sharePreference.getString(SERVER_INFO_JSON_KEY, ""), ServerList.class);
        }
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        super.processChatStatus(status);
        CUUtil.log(this, "[DEBUG CU]" + status);

        if (status == ChatService.Status.CONNECTED) {
//            login();
        } else if (status == ChatService.Status.AUTHENTICATED) {
            storeViewPreferences();
        }  else if (status == ChatService.Status.OUT_OF_GAME) {
            hideProgressDialog();
        }
    }

    @Override
    protected void onStop() {
        hideProgressDialog();
        super.onStop();
    }

    private void login() {
        showProgressDialog(getString(R.string.logging_in)).setCancelable(false);

        boolean isLoginSuccess = Registry.getChatService().login(
            EditText.class.cast(findViewById(R.id.user_id)).getText().toString(),
            EditText.class.cast(findViewById(R.id.user_password)).getText().toString()
        );

        if (!isLoginSuccess) {
            hideProgressDialog();
            EventBus.getDefault().post(new AuthenticateFailErrorEvent());
        }
    }

    public void onLoginButtonClick(View loginButton) {
        if (Registry.getChatService().isConnected() && Registry.getChatService().getChatServerInfo().getHost().equals( CURouter.getServerInfo().getXmppHost())) {
            CUUtil.log(this, "[DEBUG CU]" + Registry.getChatService().getChatServerInfo().getHost() + " " + CURouter.getServerInfo().getXmppHost());
            login();
        }
    }
}

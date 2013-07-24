package co.zoyi.carryu.Application.Views.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import co.zoyi.Chat.Datas.ChatServerInfo;
import co.zoyi.carryu.Application.Datas.Serializers.JSONSerializer;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.*;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.ValueObjects.UserLoginData;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class LoginActivity extends CUActivity {
    public static String CONNECTION_CLOSED_INTENT_KEY = "connection_closed";

    private ServerList serverList;
    private UserLoginData userLoginData;

    @Override
    protected boolean preventBackButton() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        if (ActivityDelegate.hasIntentExtra(this, CONNECTION_CLOSED_INTENT_KEY)) {
            EventBus.getDefault().post(new ConnectionClosedErrorEvent());
        }

        restoreViewPreferences();
        restoreServerInfo();
        fetchServerInfo();
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
        new ViewPreferenceManager.Builder(this)
            .put(R.id.kr_server)
            .put(R.id.na_server)
            .put(R.id.user_id)
            .save();
    }

    private void restoreViewPreferences() {
        ViewPreferenceManager.load(this);
    }

    private void storeServerInfo() {
        String serverListJsonString = JSONSerializer.getGsonInstance().toJson(this.serverList);
        SharedPreferences.Editor editor = getSharedPreferences("ServerInfo", MODE_PRIVATE).edit();
        editor.putString("server_info", serverListJsonString);
        editor.commit();
    }

    private void restoreServerInfo() {
        SharedPreferences sharePreference = getSharedPreferences("ServerInfo", MODE_PRIVATE);
        if (sharePreference.contains("server_info") == false) {
            this.serverList = JSONSerializer.getGsonInstance().fromJson(AssetReader.readString(this, "default_server_info.json"), ServerList.class);
        } else {
            this.serverList = JSONSerializer.getGsonInstance().fromJson(sharePreference.getString("server_info", ""), ServerList.class);
        }
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        super.processChatStatus(status);

        if (status == ChatService.Status.CONNECTED) {
            login();
        } else if (status == ChatService.Status.AUTHENTICATED) {
            storeViewPreferences();
        } else if (status == ChatService.Status.FAILED_AUTHENTICATE) {
            hideWaitingDialog();
            EventBus.getDefault().post(new AuthenticateFailErrorEvent());
        } else if (status == ChatService.Status.OUT_OF_GAME) {
            hideWaitingDialog();
        }
    }

    private void login() {
        userLoginData = new UserLoginData(
            EditText.class.cast(findViewById(R.id.user_id)).getText().toString(),
            EditText.class.cast(findViewById(R.id.user_password)).getText().toString()
        );

        Registry.getChatService().login(userLoginData.getUserID(), userLoginData.getUserPassword());
    }

    private void connect() {
        ServerList.ServerInfo serverInfo;
        serverInfo = getSelectedServerInfo();

        HttpRequestDelegate.setRegion(serverInfo.getRegion());

        Registry.getChatService().setServerInfo(new ChatServerInfo(serverInfo.getRegion(), serverInfo.getXmppHost(), serverInfo.getXmppPort()));
        Registry.getChatService().connect();
    }

    private ServerList.ServerInfo getSelectedServerInfo() {
        return RadioButton.class.cast(findViewById(R.id.kr_server)).isChecked() ? serverList.getKoreaServer() : serverList.getNorthAmericaServer();
    }

    public void onLoginButtonClick(View loginButton) {
        showWaitingDialog(getString(R.string.logging_in));

        if (Registry.getChatService().isConnected() &&
            Registry.getChatService().getChatServerInfo().getRegion().equals(getSelectedServerInfo())) {
            login();
        } else {
            if (Registry.getChatService().isConnected()) {
                Registry.getChatService().disconnect();
            }
            connect();
        }
    }
}

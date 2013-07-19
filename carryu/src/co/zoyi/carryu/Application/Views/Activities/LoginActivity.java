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
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.ValueObjects.UserLoginData;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class LoginActivity extends CUActivity {
    private ChatService chatService;
    private UserLoginData userLoginData;
    private ServerList serverList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CONNECTION_CLOSED")) {
            EventBus.getDefault().post(new ConnectionClosedErrorEvent());
        }

        chatService = Registry.getChatService();

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
    }

    private void restoreServerInfo() {
        SharedPreferences sharePreference = getSharedPreferences("ServerInfo", MODE_PRIVATE);
        if (sharePreference.contains("server_info") == false) {
            this.serverList = JSONSerializer.getGsonInstance().fromJson(AssetReader.readString(this, "default_server_info.json"), ServerList.class);
        } else {
            this.serverList = JSONSerializer.getGsonInstance().fromJson(sharePreference.getString("server_info", ""), ServerList.class);
        }
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        switch (event.getStatus()) {
            case CONNECTED:
                login();
                break;
            case AUTHENTICATED:
                storeViewPreferences();
                ActivityDelegate.openLobbyActivity(this);
                break;
            case FAILED_AUTHENTICATE:
                EventBus.getDefault().post(new AuthenticateFailErrorEvent());
                break;
            default:
                break;
        }
    }

    private void login() {
        chatService.login(userLoginData.getUserID(), userLoginData.getUserPassword());
    }

    private void connect() {
        if (chatService != null && chatService.isConnected()) {
            login();
        } else {
            ServerList.ServerInfo serverInfo;
            serverInfo = RadioButton.class.cast(findViewById(R.id.kr_server)).isChecked() ? serverList.getKoreaServer() : serverList.getNorthAmericaServer();

            HttpRequestDelegate.setRegion(serverInfo.getRegion());

            userLoginData = new UserLoginData(
                EditText.class.cast(findViewById(R.id.user_id)).getText().toString(),
                EditText.class.cast(findViewById(R.id.user_password)).getText().toString()
            );

            chatService.setServerInfo(new ChatServerInfo(serverInfo.getRegion(), serverInfo.getXmppHost(), serverInfo.getXmppPort()));
            chatService.connect();
        }
    }

    public void onLoginButtonClick(View loginButton) {
//        ActivityDelegate.openLobbyActivity(this);
        connect();
    }
}

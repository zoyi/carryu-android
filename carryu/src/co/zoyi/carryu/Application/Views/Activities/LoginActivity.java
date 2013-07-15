package co.zoyi.carryu.Application.Views.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.*;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.carryu.Application.Events.HttpResponses.FetchServerInfoEvent;
import co.zoyi.carryu.Application.Registries.CURegistry;
import co.zoyi.carryu.Chat.Datas.ChatServerInfo;
import co.zoyi.carryu.Chat.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.ValueObjects.UserLoginData;
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

        restoreViewPreferences();

        restoreServerInfo();
        HttpRequestDelegate.fetchServerInfo(this);
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
        String serverListJsonString = CURegistry.getSharedGsonInstance().toJson(this.serverList);
        SharedPreferences.Editor editor = getSharedPreferences("ServerInfo", MODE_PRIVATE).edit();
        editor.putString("server_info", serverListJsonString);
    }

    private void restoreServerInfo() {
        SharedPreferences sharePreference = getSharedPreferences("ServerInfo", MODE_PRIVATE);
        if (sharePreference.contains("server_info") == false) {
            this.serverList = CURegistry.getSharedGsonInstance().fromJson(AssetReader.readString(this, "default_server_info.json"), ServerList.class);
        } else {
            this.serverList = CURegistry.getSharedGsonInstance().fromJson(sharePreference.getString("server_info", ""), ServerList.class);
        }
    }

    public void onEventMainThread(FetchServerInfoEvent fetchServerInfoEvent) {
        this.serverList = fetchServerInfoEvent.getServerList();
        storeServerInfo();
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        switch (event.status) {
            case CONNECTED:
                chatService.login(userLoginData.getUserID(), userLoginData.getUserPassword());
                break;
            case AUTHENTICATED:
                storeViewPreferences();
                ActivityDelegate.openLobbyActivity(this);
                break;
            default:
                break;
        }
    }

    public void onLoginButtonClick(View loginButton) {
        HttpRequestDelegate.setRegion("na");
        ActivityDelegate.openLobbyActivity(this);

//        ServerList.ServerInfo serverInfo;
//        String region;
//        if (RadioButton.class.cast(findViewById(R.id.kr_server)).isChecked()) {
//            serverInfo = serverList.getKoreaServer();
//            region = "kr";
//        } else {
//            serverInfo = serverList.getNorthAmericaServer();
//            region = "na";
//        }
//
//        HttpRequestDelegate.setRegion(region);
//
//        userLoginData = new UserLoginData(
//            EditText.class.cast(findViewById(R.id.user_id)).getText().toString(),
//            EditText.class.cast(findViewById(R.id.user_password)).getText().toString()
//        );
//
//        chatService = CURegistry.getChatService();
//        chatService.setServerInfo(new ChatServerInfo(region, serverInfo.getXmppHost(), serverInfo.getXmppPort()));
//        chatService.connect();
    }
}

package co.zoyi.carryu.Application.Views.Activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import co.zoyi.Chat.Datas.ChatServerInfo;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Datas.Serializers.JSONSerializer;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.AssetReader;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.SelectBoxDialog;
import co.zoyi.carryu.R;

import java.util.HashMap;
import java.util.Map;

public class ServerSelectActivity extends CUActivity {
    public static String EXIT_APPLICATION_INTENT_KEY = "exit_application";

    private final String SERVER_INFO_PREFERENCE_KEY = "server_info_pref";
    private final String SERVER_INFO_JSON_KEY = "server_info";
    private final String PREF_SELECTED_SERVER_KEY = "pref_selected_server";

    private final int SELECTED_SERVER_KEY = 987654321;
    private final String DEFAULT_SERVER_INFO_JSON_FILE = "default_server_info.json";

    private ServerList serverList;
    private SelectBoxDialog selectBoxDialog = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityDelegate.hasIntentExtra(this, EXIT_APPLICATION_INTENT_KEY)) {
            if (Registry.getChatService().isConnected()) {
                Registry.getChatService().disconnect();
            }
        }

        setContentView(R.layout.server_select_activity);

        restoreServerInfo();
        restoreSelectedServer();
        fetchServerInfo();
    }

    private void fetchServerInfo() {
        HttpRequestDelegate.fetchServerInfo(new DataCallback<ServerList>() {
            @Override
            public void onSuccess(ServerList serverList) {
                super.onSuccess(serverList);
                ServerSelectActivity.this.serverList = serverList;
                storeServerInfo();
            }
        });
    }
    private void storeSelectedServer() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_SELECTED_SERVER_KEY, MODE_PRIVATE);
        ServerList.ServerInfo.ServerName selected_server = (ServerList.ServerInfo.ServerName)(Button.class.cast(findViewById(R.id.server_select_btn)).getTag(SELECTED_SERVER_KEY));
        sharedPreferences.edit().putInt(PREF_SELECTED_SERVER_KEY, selected_server.ordinal()).commit();  // server_name => pref_int
    }

    private void restoreSelectedServer() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_SELECTED_SERVER_KEY, MODE_PRIVATE);
        int selected_server = sharedPreferences.getInt(PREF_SELECTED_SERVER_KEY, ServerList.ServerInfo.ServerName.KR.ordinal());
        changeServerSelectButton(ServerList.ServerInfo.ServerName.values()[selected_server]);    // pref_int => server_name
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
        storeSelectedServer();

        if (status == ChatService.Status.CONNECTED) {
            hideProgressDialog();
            ActivityDelegate.openLoginSelectActivity(ServerSelectActivity.this);
        }

    }

    @Override
    protected void onStop() {
        hideProgressDialog();
        super.onStop();
    }

    private void connect() {
        showProgressDialog(getString(R.string.connecting));

        ServerList.ServerInfo serverInfo = getSelectedServerInfo();
        CURouter.setServerInfo(serverInfo);

        Registry.getChatService().setServerInfo(new ChatServerInfo(serverInfo.getXmppHost(), serverInfo.getXmppPort()));
        new AsyncTask<ChatService, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ChatService... chatServices) {
                ChatService chatService = chatServices[0];
                return new Boolean(chatService.connect());
            }
        }.execute(Registry.getChatService());
    }

    private ServerList.ServerInfo getSelectedServerInfo() {
        Button serverSelectBtn = Button.class.cast(findViewById(R.id.server_select_btn));
        switch ((ServerList.ServerInfo.ServerName)serverSelectBtn.getTag(SELECTED_SERVER_KEY)  ) {
            case KR:
                return serverList.getKoreaServer();
            case NA:
                return serverList.getNorthAmericaServer();
            case EUW:
                return serverList.getEuropeWestServer();
        }
        return serverList.getKoreaServer();
    }

    private void changeServerSelectButton(ServerList.ServerInfo.ServerName serverName) {
        switch (serverName) {
            case KR :
                Button.class.cast(findViewById(R.id.server_select_btn)).setText(getString(R.string.kr_server));
                Button.class.cast(findViewById(R.id.server_select_btn)).setTag(SELECTED_SERVER_KEY, ServerList.ServerInfo.ServerName.KR);
                break;
            case NA:
                Button.class.cast(findViewById(R.id.server_select_btn)).setText(getString(R.string.na_server));
                Button.class.cast(findViewById(R.id.server_select_btn)).setTag(SELECTED_SERVER_KEY, ServerList.ServerInfo.ServerName.NA);
                break;
            case EUW:
                Button.class.cast(findViewById(R.id.server_select_btn)).setText(getString(R.string.euw_server));
                Button.class.cast(findViewById(R.id.server_select_btn)).setTag(SELECTED_SERVER_KEY, ServerList.ServerInfo.ServerName.EUW);
                break;
        }
    }

    private void showServerSelectBoxDialog() {
        if (selectBoxDialog == null) {
            Map<Object, String> map = new HashMap<Object, String>();
            map.put(ServerList.ServerInfo.ServerName.EUW, getString(R.string.euw_server));
            map.put(ServerList.ServerInfo.ServerName.NA, getString(R.string.na_server));
            map.put(ServerList.ServerInfo.ServerName.KR, getString(R.string.kr_server));

            selectBoxDialog = new SelectBoxDialog(this, getString(R.string.select_region), map, ServerList.ServerInfo.ServerName.class.cast(Button.class.cast(findViewById(R.id.server_select_btn)).getTag(SELECTED_SERVER_KEY)).ordinal()
                , new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    changeServerSelectButton(ServerList.ServerInfo.ServerName.values()[i]);
                }
            });
        }
        selectBoxDialog.show();
    }

    public void onNextButtonClick(View nextButton) {
        if (Registry.getChatService().isConnected()) {
            Registry.getChatService().disconnect();
        }
        connect();
    }

    public void onSelectButtonClick(View selectButton) {
        showServerSelectBoxDialog();
    }
}

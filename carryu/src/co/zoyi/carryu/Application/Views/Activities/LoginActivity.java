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
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Etc.ViewPreferenceManager;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class LoginActivity extends CUActivity {
    public static String CONNECTION_CLOSED_INTENT_KEY = "connection_closed";

    private String NOTICE_PREFERENCE_KEY = "notice_pref";
    private String NOTICE_PASSWORD_KEY = "notice_password_key";

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

    @Override
    protected void processChatStatus(ChatService.Status status) {
        super.processChatStatus(status);
        CUUtil.log(this, "[DEBUG CU]" + status);

        if (status == ChatService.Status.CONNECTED) {
//            login();
        } else if (status == ChatService.Status.AUTHENTICATED) {
            storeViewPreferences();
            hideProgressDialog();
        }  else if (status == ChatService.Status.OUT_OF_GAME) {
            hideProgressDialog();
        }
    }

    @Override
    protected void onStop() {
        hideProgressDialog();
        super.onStop();
    }

    private void reconnect() {
        ServerList.ServerInfo serverInfo = CURouter.getServerInfo();
        Registry.getChatService().setServerInfo(new ChatServerInfo(serverInfo.getXmppHost(), serverInfo.getXmppPort()));
        Registry.getChatService().connect();
    }

    private void login() {
        showProgressDialog(getString(R.string.logging_in)).setCancelable(false);

        new AsyncTask<ChatService, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ChatService... chatServices) {
                ChatService chatService = chatServices[0];
                if (!chatService.isConnected()) {
                    reconnect();
                }
                boolean isLoginSuccess = chatService.login(
                        EditText.class.cast(findViewById(R.id.user_id)).getText().toString(),
                        EditText.class.cast(findViewById(R.id.user_password)).getText().toString());
                if (!isLoginSuccess) {
                    hideProgressDialog();
                    EventBus.getDefault().post(new AuthenticateFailErrorEvent());
                    reconnect();
                }
                return new Boolean(isLoginSuccess);
            }
        }.execute(Registry.getChatService());
    }

    public void onLoginButtonClick(View loginButton) {
        if (Registry.getChatService().getChatServerInfo().getHost().equals( CURouter.getServerInfo().getXmppHost())) {
            if (Registry.getChatService().isConnected()) {
                login();
            }
        }
    }
}

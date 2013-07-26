package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.ErrorCroutonDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Events.NotifyMeChangedEvent;
import co.zoyi.carryu.Application.Events.Errors.ErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
import co.zoyi.carryu.Application.Views.Dialogs.ConfirmDialog;
import co.zoyi.carryu.Application.Views.Dialogs.MessageDialog;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public abstract class CUActivity extends FragmentActivity {
    public static String CONFIRM_MESSAGE_INTENT_KEY = "confirm_message";

    private static Summoner me;
    private MessageDialog messageDialog;

    protected boolean shouldConfirmBeforeFinish() {
        return true;
    }

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityDelegate.openSearchSummonerActivity(CUActivity.this);
        }
    };

    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (CUActivity.this instanceof Refreshable) {
                ((Refreshable)CUActivity.this).refresh();
            }
        }
    };

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        CUUtil.setFontAllView((ViewGroup) getWindow().getDecorView());

        View searchButton = findViewById(R.id.search);
        if (searchButton != null) {
            searchButton.setOnClickListener(searchClickListener);
        }

        View refreshButton = findViewById(R.id.refresh);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(refreshClickListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityDelegate.hasIntentExtra(this, CONFIRM_MESSAGE_INTENT_KEY)) {
            AlertDialog alertDialog = new AlertDialog(this, getIntent().getStringExtra(CONFIRM_MESSAGE_INTENT_KEY));
            alertDialog.show();
            ActivityDelegate.removeIntentExtra(this, CONFIRM_MESSAGE_INTENT_KEY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        if (me == null) {
            fetchMe();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void hideProgressDialog() {
        if (messageDialog != null && messageDialog.isShowing()) {
            messageDialog.dismiss();
            messageDialog = null;
        }
    }

    protected MessageDialog showProgressDialog() {
        return showProgressDialog(getString(R.string.loading));
    }

    protected MessageDialog showProgressDialog(String message) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(this, message);
        } else {
            messageDialog.setMessage(message);
        }

        if (messageDialog.isShowing() == false) {
            messageDialog.show();
        }

        return messageDialog;
    }

    private static void fetchMe() {
        if (Registry.getChatService().getUserId() != null) {
            HttpRequestDelegate.fetchSummoner(Registry.getChatService().getUserId(), new DataCallback<Summoner>() {
                @Override
                public void onSuccess(Summoner object) {
                    super.onSuccess(object);
                    if (object != null) {
                        setMe(object);
                    }
                }
            });
        }
    }

    public static void setMe(Summoner me) {
        CUActivity.me = me;
        EventBus.getDefault().post(new NotifyMeChangedEvent(me));
    }

    @Override
    public void onBackPressed() {
        if (shouldConfirmBeforeFinish()) {
            ConfirmDialog confirmDialog = new ConfirmDialog(this, getString(R.string.exit_app));
            confirmDialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
                @Override
                public void onConfirm() {
                    finish();
                }

                @Override
                public void onCancel() {
                }
            });
            confirmDialog.show();
        } else {
            finish();
        }
    }

    public static Summoner getMe() {
        if (me == null) {
            fetchMe();
        }
        return me;
    }

    public void onEventMainThread(ErrorEvent errorEvent) {
        ErrorCroutonDelegate.showErrorMessage(this, errorEvent);
    }

    protected void processChatStatus(ChatService.Status status) {
        if (status == ChatService.Status.CHAMPION_SELECT) {
            if (getClass() != ChampionSelectActivity.class && getClass() == LobbyActivity.class) {
                ActivityDelegate.openChampionSelectActivity(this);
            }
        } else if (status == ChatService.Status.IN_GAME) {
            if (getClass() != InGameActivity.class) {
                ActivityDelegate.openInGameActivity(this);
            }
        } else if (status == ChatService.Status.OUT_OF_GAME || status == ChatService.Status.IN_QUEUE || status == ChatService.Status.AUTHENTICATED) {
            if (getClass() != LobbyActivity.class) {
                ActivityDelegate.openLobbyActivity(this);
            }
        } else if (status == ChatService.Status.CONNECTION_CLOSED){
            if (getClass() != LoginActivity.class) {
                ActivityDelegate.openLoginActivityWithConnectionClosedCrouton(this);
            }
        }
    }

    public void setTitle(String title) {
        CUUtil.log(this, "setTitle: " + title.toString());
        View titleView = findViewById(R.id.title);
        if (titleView != null && titleView instanceof TextView) {
            TextView.class.cast(titleView).setText(title);
        } else {
            CUUtil.log(this, "Title is not TextView");
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
//        Crouton.makeText(this, event.getStatus().toString(), CUCroutonStyle.INFO).show();
        processChatStatus(event.getStatus());
    }
}

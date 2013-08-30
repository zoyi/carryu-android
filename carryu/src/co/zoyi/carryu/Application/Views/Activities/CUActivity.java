    package co.zoyi.carryu.Application.Views.Activities;

    import android.os.Bundle;
    import android.support.v4.app.FragmentActivity;
    import android.view.*;
    import android.view.View.OnClickListener;
    import android.widget.TextView;
    import co.zoyi.Chat.Services.ChatService;
    import co.zoyi.carryu.Application.API.DataCallback;
    import co.zoyi.carryu.Application.API.HttpRequestDelegate;
    import co.zoyi.carryu.Application.Datas.Models.Summoner;
    import co.zoyi.carryu.Application.Etc.ActivityDelegate;
    import co.zoyi.carryu.Application.Etc.CUUtil;
    import co.zoyi.carryu.Application.Etc.ErrorCroutonDelegate;
    import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
    import co.zoyi.carryu.Application.Events.Errors.ErrorEvent;
    import co.zoyi.carryu.Application.Events.NotifyMeChangedEvent;
    import co.zoyi.carryu.Application.Registries.Registry;
    import co.zoyi.carryu.Application.Views.Commons.Refreshable;
    import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
    import co.zoyi.carryu.Application.Views.Dialogs.ConfirmDialog;
    import co.zoyi.carryu.Application.Views.Dialogs.MessageDialog;
    import co.zoyi.carryu.R;
    import com.google.analytics.tracking.android.EasyTracker;
    import de.greenrobot.event.EventBus;

public abstract class CUActivity extends FragmentActivity {
    public static String CONFIRM_MESSAGE_INTENT_KEY = "confirm_message";

    protected Summoner me;
//    private static String keepScreenOnKey = "keep_screen_on";

    private MessageDialog messageDialog;

    private OnClickListener menuClickListener;
    private OnClickListener backClickListener;
    private OnClickListener searchClickListener;
    private OnClickListener refreshClickListener;

//    private SharedPreferences getKeepScreenPreference() {
//        return getSharedPreferences("keep_screen_preference", MODE_PRIVATE);
//    }

    public OnClickListener getRefreshClickListener() {
        if (this.refreshClickListener == null) {
            this.refreshClickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CUActivity.this instanceof Refreshable) {
                        ((Refreshable)CUActivity.this).refresh();
                    }
                }
            };
        }
        return this.refreshClickListener;
    }

    private OnClickListener getSearchClickListener() {
        if (this.searchClickListener == null) {
            this.searchClickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityDelegate.openSearchSummonerActivity(CUActivity.this);
                }
            };
        }
        return this.searchClickListener;
    }

    public OnClickListener getMenuClickListener() {
        if (this.menuClickListener == null) {
            this.menuClickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionsMenu();
                }
            };
        }
        return this.menuClickListener;
    }

    public OnClickListener getBackClickListener() {
        if (this.backClickListener == null) {
            this.backClickListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            };
        }
        return backClickListener;
    }

    private void addClickListenerIfViewExist(int viewId, OnClickListener listener) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

//    private void setKeepScreeOn(boolean isKeepScreenOn) {
//        CUUtil.log(this, String.format("setKeepScreeOn: " + String.valueOf(isKeepScreenOn)));
//
//        if (isKeepScreenOn) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        } else {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
//        getKeepScreenPreference().edit().putBoolean(keepScreenOnKey, isKeepScreenOn).commit();
//    }
//
//    private void resetKeepScreenMenuItemTitle(MenuItem item) {
//        if (getKeepScreenPreference().getBoolean(keepScreenOnKey, true)) {
//            item.setTitle(R.string.keep_screen_on);
//        } else {
//            item.setTitle(R.string.keep_screen_off);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Registry.getChatService().disconnect();
                ActivityDelegate.openServerSelectActivity(this);
                break;
            case R.id.feedback:
                ActivityDelegate.openFeedbackActivity(this);
                break;
//            case R.id.keep_screen_toggle:
//                setKeepScreeOn(!getKeepScreenPreference().getBoolean(keepScreenOnKey, true));
//                resetKeepScreenMenuItemTitle(item);
//                break;
            case R.id.search:
                ActivityDelegate.openSearchSummonerActivity(this);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (findViewById(R.id.menu) != null) {
            getMenuInflater().inflate(R.menu.menu, menu);
    //        resetKeepScreenMenuItemTitle(menu.findItem(R.id.keep_screen_toggle));
            return true;
        }
        return false;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        CUUtil.setFontAllView((ViewGroup) getWindow().getDecorView());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setKeepScreeOn(getKeepScreenPreference().getBoolean(keepScreenOnKey, true));

        addClickListenerIfViewExist(R.id.menu, getMenuClickListener());
        addClickListenerIfViewExist(R.id.back, getBackClickListener());
        addClickListenerIfViewExist(R.id.search, getSearchClickListener());
        addClickListenerIfViewExist(R.id.refresh, getRefreshClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityDelegate.hasIntentExtra(this, CONFIRM_MESSAGE_INTENT_KEY)) {
            AlertDialog alertDialog = new AlertDialog(this, getIntent().getStringExtra(CONFIRM_MESSAGE_INTENT_KEY));
            alertDialog.show();
            ActivityDelegate.removeIntentExtra(this, CONFIRM_MESSAGE_INTENT_KEY);
        }

        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (me == null) {
            fetchMe();
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public void setTitle(String title) {
        View titleView = findViewById(R.id.title);
        if (titleView != null && titleView instanceof TextView) {
            TextView.class.cast(titleView).setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
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

    public Summoner getMe() {
        if (me == null) {
            fetchMe();
        }
        return me;
    }

    public void setMe(Summoner me) {
        this.me = me;
        EventBus.getDefault().post(new NotifyMeChangedEvent(me));
    }

    protected void fetchMe() {
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

    protected boolean shouldConfirmBeforeFinish() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (shouldConfirmBeforeFinish()) {
            ConfirmDialog confirmDialog = new ConfirmDialog(this, getString(R.string.exit_app));
            confirmDialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
                @Override
                public void onConfirm() {
                    ActivityDelegate.exitApplication(CUActivity.this);
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
//            if (getClass() != LoginActivity.class) {
//                ActivityDelegate.openLoginActivityWithConnectionClosedCrouton(this);
//            }
        }
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        // 아마 사용 안함.
        processChatStatus(event.getStatus());
    }

    public void onEventMainThread(ErrorEvent errorEvent) {
        ErrorCroutonDelegate.showErrorMessage(this, errorEvent);
    }
}

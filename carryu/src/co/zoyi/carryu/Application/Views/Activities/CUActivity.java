package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.ErrorCroutonDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Events.UpdatedMeEvent;
import co.zoyi.carryu.Application.Events.Errors.ErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.MessageDialog;
import de.greenrobot.event.EventBus;

public abstract class CUActivity extends FragmentActivity {
    private static Summoner me;
    private int countOfWaitingDialog = 0;
    private MessageDialog messageDialog;

    protected boolean preventBackButton() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        CUUtil.setFontAllView((ViewGroup) getWindow().getDecorView());
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

    protected void hideWaitingDialog() {
        if (--countOfWaitingDialog == 0) {
            messageDialog.dismiss();
        }
    }

    protected void showWaitingDialog() {
        if (++countOfWaitingDialog == 1) {
            messageDialog = new MessageDialog(this);
            messageDialog.show();
        }
    }

    protected void showWaitingDialog(String message) {
        if (++countOfWaitingDialog == 1) {
            messageDialog = new MessageDialog(this, message);
            messageDialog.show();
        }
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
        EventBus.getDefault().post(new UpdatedMeEvent(me));
    }

    @Override
    public void onBackPressed() {
        if (preventBackButton() == false) {
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
            if (getClass() != ChampionSelectActivity.class) {
                ActivityDelegate.openChampionSelectActivity(this);
            }
        } else if (status == ChatService.Status.IN_GAME) {
            if (getClass() != InGameActivity.class) {
                ActivityDelegate.openInGameActivity(this);
            }
        } else if (status == ChatService.Status.OUT_OF_GAME || status == ChatService.Status.IN_QUEUE) {
            if (getClass() != LobbyActivity.class) {
                ActivityDelegate.openLobbyActivity(this);
            }
        } else if (status == ChatService.Status.CONNECTION_CLOSED){
            if (getClass() != LoginActivity.class) {
                ActivityDelegate.openLoginActivityWithConnectionClosedCrouton(this);
            }
        }
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
//        Crouton.makeText(this, event.getStatus().toString(), CUCroutonStyle.INFO).show();
        processChatStatus(event.getStatus());
    }
}

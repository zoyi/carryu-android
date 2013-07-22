package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUCroutonStyle;
import co.zoyi.carryu.Application.Etc.ErrorCroutonDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Events.Errors.ErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class CUActivity extends FragmentActivity {
    private static Summoner me;
    private int countOfWaitingDialog = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        CUUtil.setFontAllView((ViewGroup) getWindow().getDecorView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (me == null) {
            fetchMe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countOfWaitingDialog > 0) {
        }
    }

    protected void hideWaitingDialog() {
        --countOfWaitingDialog;
    }

    protected void showWaitingDialog() {
        ++countOfWaitingDialog;
    }

    private static void fetchMe() {
        if (Registry.getChatService().getUserId() != null) {
            HttpRequestDelegate.fetchSummoner(Registry.getChatService().getUserId(), new DataCallback<Summoner>() {
                @Override
                public void onSuccess(Summoner object) {
                    super.onSuccess(object);
                    me = object;
                }
            });
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

    public void onEventMainThread(ChatStatusChangeEvent event) {
        Crouton.makeText(this, event.getStatus().toString(), CUCroutonStyle.INFO).show();

        if (event.getStatus() == ChatService.Status.CHAMPION_SELECT) {
            if (getClass() != ChampionSelectActivity.class) {
                ActivityDelegate.openChampionSelectActivity(this);
            }
        } else if (event.getStatus() == ChatService.Status.IN_GAME) {
            if (getClass() != InGameActivity.class) {
                ActivityDelegate.openInGameActivity(this);
            }
        } else if (event.getStatus() == ChatService.Status.OUT_OF_GAME ||
            event.getStatus() == ChatService.Status.IN_QUEUE ) {
            if (getClass() != LobbyActivity.class) {
                ActivityDelegate.openLobbyActivity(this);
            }
        } else if (event.getStatus() == ChatService.Status.CONNECTION_CLOSED){
            ActivityDelegate.openLoginActivityWithConnectionClosedCrouton(this);
        }
    }
}

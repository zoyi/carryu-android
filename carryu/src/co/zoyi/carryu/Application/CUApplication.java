package co.zoyi.carryu.Application;

import android.app.Application;
import android.content.Context;
import co.zoyi.Chat.Listeners.ChatStatusChangeListener;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

public class CUApplication extends Application {
    private static CUApplication instance;

    public CUApplication() {
        super();
        instance = this;
    }

    @Override
    public void onTerminate() {
//        Registry.getChatService().disconnect();
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Registry.initialize();
        Registry.getChatService().setChatStatusChangeListener(new ChatStatusChangeListener() {
            @Override
            public void onStatusChanged(ChatService.Status status) {
                EventBus.getDefault().post(new ChatStatusChangeEvent(status));
            }
        });
        Registry.getChatService().setDefaultOnlineStatusMessage(getString(R.string.online_status_message));

        CUUtil.loadFonts();
    }

    public static Context getContext() {
        return instance;
    }
}

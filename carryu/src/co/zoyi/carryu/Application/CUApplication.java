package co.zoyi.carryu.Application;

import android.app.Application;
import android.content.Context;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Registries.CURegistry;

public class CUApplication extends Application {
    private static CUApplication instance;

    public CUApplication() {
        super();
        instance = this;
    }

    @Override
    public void onTerminate() {
        CURegistry.getChatService().disconnect();
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CURegistry.initialize();
        CUUtil.loadFonts();
    }

    public static Context getContext() {
        return instance;
    }
}

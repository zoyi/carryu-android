package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import co.zoyi.carryu.Application.Etc.ErrorCroutonDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.Errors.CUErrorEvent;
import de.greenrobot.event.EventBus;

public class CUActivity extends FragmentActivity {
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

    public void onEvent(CUErrorEvent errorEvent) {
        ErrorCroutonDelegate.showErrorMessage(this, errorEvent);
    }
}

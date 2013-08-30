package co.zoyi.carryu.Application.Views.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Etc.ViewPreferenceManager;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

/**
 * Created with IntelliJ IDEA.
 * User: huy
 * Date: 8/28/13
 * Time: 5:29 오후
 * To change this template use File | Settings | File Templates.
 */
public class SummonerIdLoginActivity extends CUActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summoner_id_login_activity);

        restoreViewPreferences();
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return false;
    }

    private void storeViewPreferences() {
        new ViewPreferenceManager.Storage(this)
                .save(R.id.user_id, "user_id");
    }

    private void restoreViewPreferences() {
        new ViewPreferenceManager.Loader(this)
                .load(R.id.user_id, "user_id");
    }

    public void onSummonerIdLoginButtonClick(View loginButton) {
        storeViewPreferences();
        ActivityDelegate.openInGameActivityWithSummonerId(this, EditText.class.cast(findViewById(R.id.user_id)).getText().toString());
    }
}
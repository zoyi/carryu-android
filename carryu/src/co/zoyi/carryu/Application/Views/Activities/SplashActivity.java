package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.R;

public class SplashActivity extends CUActivity {
    private static final int SPLASH_DURATION = 2000;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                ActivityDelegate.openServerSelectActivity(SplashActivity.this);
            }
        }, SPLASH_DURATION);
    }
}

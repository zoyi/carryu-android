package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.Application.Views.Fragments.WebViewFragment;
import co.zoyi.carryu.R;

public class FacebookPageActivity extends CUActivity implements Refreshable {
    private WebViewFragment webViewFragment;

    protected boolean shouldConfirmBeforeFinish() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        this.webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.web_view_fragment);
        refresh();
    }

    @Override
    public void refresh() {
        this.webViewFragment.loadUrl(CURouter.getFeedbackURL());
    }
}

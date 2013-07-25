package co.zoyi.carryu.Application.Views.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Datas.Models.ActiveGame;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Events.UpdatedMeEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Dialogs.AlertDialog;
import co.zoyi.carryu.Application.Views.Fragments.Refreshable;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.Application.Views.Fragments.WebViewFragment;
import co.zoyi.carryu.R;

public class InGameActivity extends CUActivity implements TabHost.OnTabChangeListener {
    private boolean isSample;
    public static String SAMPLE_IN_GAME_INTENT_KEY = "is_sample";

    private TabHost tabHost;
    private ActiveGame activeGame;
    private Fragment lastFragment;
    private WebViewFragment championGuideFragment;
    private SummonerListFragment ourTeamListFragment;
    private SummonerListFragment enemyTeamListFragment;

    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (lastFragment != null) {
                ((Refreshable) lastFragment).refresh();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.in_game_activity);

        if (ActivityDelegate.hasIntentExtra(this, SAMPLE_IN_GAME_INTENT_KEY)) {
            isSample = getIntent().getBooleanExtra(SAMPLE_IN_GAME_INTENT_KEY, false);
        }

        findViewById(R.id.refresh).setOnClickListener(refreshClickListener);

        initializeTabs();

        showFragment(ourTeamListFragment);
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        if (event.getFragment().getClass() == SummonerListFragment.class) {
            startFetchSummonersInGame((SummonerListFragment) event.getFragment());
        }
    }

    public void onEventMainThread(UpdatedMeEvent event) {
        if (lastFragment == ourTeamListFragment || lastFragment == enemyTeamListFragment) {
            startFetchSummonersInGame((SummonerListFragment) lastFragment);
        }
    }

    private void startFetchSummonersInGame(final SummonerListFragment fragment) {
        if (this.activeGame == null) {
            if (isSample == false) {
                if (getMe() != null) {
                    HttpRequestDelegate.fetchActiveGame(getMe(), new DataCallback<ActiveGame>() {
                        @Override
                        public void onSuccess(ActiveGame activeGame) {
                            super.onSuccess(activeGame);
                            updateActiveGameAndSummoners(activeGame, fragment);
                        }
                    });
                }
            } else {
                HttpRequestDelegate.fetchActiveGameSample(new DataCallback<ActiveGame>() {
                    @Override
                    public void onSuccess(ActiveGame activeGame) {
                        super.onSuccess(activeGame);
                        updateActiveGameAndSummoners(activeGame, fragment);
                    }
                });
            }
        } else {
            updateActiveGameAndSummoners(this.activeGame, fragment);
        }
    }

    private void updateActiveGameAndSummoners(ActiveGame activeGame, SummonerListFragment fragment) {
        if (activeGame != null) {
            this.activeGame = activeGame;

            if (fragment == ourTeamListFragment) {
                fragment.updateSummoners(activeGame.getOurTeamSummoners());
            } else {
                fragment.updateSummoners(activeGame.getEnemyTeamSummoners());
            }

            if (isSample == false) {
                for (Summoner summoner : activeGame.getOurTeamSummoners()) {
                    if (summoner.getId() == Integer.parseInt(Registry.getChatService().getUserId())) {
                        championGuideFragment.loadUrl(String.format("http://%s.carryu.co/champions/%d/guides", Registry.getChatService().getChatServerInfo().getRegion(), summoner.getChampion().getId()));
                    }
                }
            } else {
                championGuideFragment.loadUrl(String.format("http://%s.carryu.co/champions/98/guides", Registry.getChatService().getChatServerInfo().getRegion()));
            }
        } else {
            finish();

            if (isSample == false) {
                ActivityDelegate.openActivityWithConfirmMessage(this, LobbyActivity.class, getString(R.string.not_support_ai_mode));
            } else {
                ActivityDelegate.openActivityWithConfirmMessage(this, LobbyActivity.class, getString(R.string.no_response_error));
            }
        }
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        if (isSample == false) {
            super.processChatStatus(status);
        }
    }

    private WebViewFragment.WebViewStatusChangeListener webViewStatusChangeListener = new WebViewFragment.WebViewStatusChangeListener() {
        @Override
        public void onPageStarted(WebView webView) {
            setTitle(R.string.loading);
        }

        @Override
        public void onPageFinished(WebView webView) {
            setTitle(R.string.app_name);
        }
    };

    private void initializeTabs() {
        if (tabHost == null) {
            tabHost = (TabHost) findViewById(android.R.id.tabhost);
            tabHost.setup();
            tabHost.setOnTabChangedListener(this);

            addTab(R.string.our_summoner, R.drawable.ico_star);
            addTab(R.string.enemy_summoner, R.drawable.ico_heart);
            addTab(R.string.champion_guide, R.drawable.ico_list);

            ourTeamListFragment = new SummonerListFragment();
            enemyTeamListFragment = new SummonerListFragment();
            championGuideFragment = new WebViewFragment();
            championGuideFragment.setWebViewStatusChangeListener(webViewStatusChangeListener);
        }
    }

    private void showFragment(Fragment fragment) {
        if (fragment != lastFragment) {
            lastFragment = fragment;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setTitle(R.string.app_name);
        }
    }

    @Override
    public void onTabChanged(String tag) {
        Fragment fragment = null;

        if (tag.equals(getString(R.string.our_summoner))) {
            fragment = ourTeamListFragment;
        } else if (tag.equals(getString(R.string.enemy_summoner))) {
            fragment = enemyTeamListFragment;
        } else if (tag.equals(getString(R.string.champion_guide))) {
            fragment = championGuideFragment;
        }

        showFragment(fragment);
    }

    private void addTab(int labelID, int drawableId) {
        String tag = getString(labelID);

        TabHost.TabSpec spec = tabHost.newTabSpec(tag);

        View tabIndicator = LayoutInflater.from(this).inflate(
            R.layout.tab_indicator,
            ViewGroup.class.cast(findViewById(android.R.id.tabs)),
            false
        );

        TextView.class.cast(tabIndicator.findViewById(R.id.title)).setText(labelID);
        ImageView.class.cast(tabIndicator.findViewById(R.id.icon)).setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(this.new TabFactory(this));

        tabHost.addTab(spec);
    }

    class TabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        if (lastFragment != championGuideFragment ||
            championGuideFragment.goBack() == false) {
            super.onBackPressed();
        }
    }
}

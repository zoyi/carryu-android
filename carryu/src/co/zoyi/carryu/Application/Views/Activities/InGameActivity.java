package co.zoyi.carryu.Application.Views.Activities;

import android.content.Context;
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
import co.zoyi.carryu.Application.API.DataCallback;
import co.zoyi.carryu.Application.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Datas.Models.ActiveGame;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CURouter;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Events.NotifyMeChangedEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.Application.Views.Fragments.WebViewFragment;
import co.zoyi.carryu.R;

import java.util.List;

public class InGameActivity extends CUActivity implements TabHost.OnTabChangeListener, Refreshable {
    public static String SAMPLE_IN_GAME_INTENT_KEY = "is_sample";
    public static String IN_GAME_WITH_SUMMONER_ID_INTENT_KEY = "in_game_with_summoner_id";

    private TabHost tabHost;
    private boolean isSampleMode;
    private String summonerIdWithoutLogin;

    private ActiveGame activeGame;
    private Fragment lastFragment;
    private String championGuideUrl;
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

    private boolean isLastFragmentSummonerListFragment() {
        return lastFragment == ourTeamListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_game_activity);

        if (getIntent() != null) {
            summonerIdWithoutLogin = getIntent().getStringExtra(IN_GAME_WITH_SUMMONER_ID_INTENT_KEY);
            isSampleMode = getIntent().getBooleanExtra(SAMPLE_IN_GAME_INTENT_KEY, false);
        }
        initializeTabs();
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        if (status == ChatService.Status.CONNECTION_CLOSED) {
            super.processChatStatus(status);
        } else if (isSampleMode == false) {
            super.processChatStatus(status);
        }
    }

    @Override
    protected boolean shouldConfirmBeforeFinish() {
        return isAvailableSession();
    }

    @Override
    public void onBackPressed() {
        if (isLastFragmentSummonerListFragment() || championGuideFragment.goBack() == false) {
            super.onBackPressed();
        }
    }

    @Override
    protected void fetchMe() {
        if (isAvailableSession()) {
            HttpRequestDelegate.fetchSummoner(Registry.getChatService().getUserId(), new DataCallback<Summoner>() {
                @Override
                public void onSuccess(Summoner object) {
                    super.onSuccess(object);
                    if (object != null) {
                        setMe(object);
                    }
                }
            });
        } else if(summonerIdWithoutLogin != null) {
            HttpRequestDelegate.fetchSummonerWithName(summonerIdWithoutLogin, new DataCallback<Summoner>() {
                @Override
                public void onSuccess(Summoner object) {
                    super.onSuccess(object);
                    if (object != null) {
                        setMe(object);
                    }
                }
            });
        } else {
            //TODO ERROR CAN NOT FETCH ME
        }
    }

    private String getChampionGuideUrl() {
        if (this.championGuideUrl == null) {
            if (isSampleMode) {
                this.championGuideUrl = CURouter.getChampionGuideURL(98);
            } else {
                int championId = 0;
                for (Summoner summoner : activeGame.getOurTeamSummoners()) {
                    if (summoner.getId() == getMe().getId()) {
                        championId = summoner.getChampion().getId();
                    }
                }
                this.championGuideUrl = CURouter.getChampionGuideURL(championId);
            }
        }

        return this.championGuideUrl;
    }

    private boolean isAvailableSession() {
        return Registry.getChatService().getUserId() != null;
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        if (event.getFragment().getClass() == SummonerListFragment.class) {
            if (this.activeGame == null) {
                fetchActiveGame();
            } else {
                List<Summoner> summoners;
                if (event.getFragment() == ourTeamListFragment) {
                    summoners = this.activeGame.getOurTeamSummoners();
                } else {
                    summoners = this.activeGame.getEnemyTeamSummoners();
                }
                SummonerListFragment.class.cast(event.getFragment()).updateSummoners(summoners);
            }
        } else if (event.getFragment().getClass() == WebViewFragment.class) {
            championGuideFragment.loadUrl(getChampionGuideUrl());
        }
    }

    public void onEventMainThread(NotifyMeChangedEvent event) {
        if (isSampleMode == false && this.activeGame == null) {
            fetchActiveGame();
        }
    }


    private void fetchActiveGame() {
        if (this.activeGame == null) {
            final DataCallback<ActiveGame> dataCallback = new DataCallback<ActiveGame>() {
                @Override
                public void onSuccess(ActiveGame activeGame) {
                    super.onSuccess(activeGame);
                    updateActiveGame(activeGame);
                }
            };

            if (isSampleMode) {
                HttpRequestDelegate.fetchActiveGameSample(dataCallback);
            } else if (getMe() != null) {
                HttpRequestDelegate.fetchActiveGame(getMe(), dataCallback);
            }
        } else {
            updateActiveGame(this.activeGame);
        }
    }

    private void updateActiveGame(ActiveGame activeGame) {

        if (activeGame != null) {
            this.activeGame = activeGame;
            ourTeamListFragment.updateSummoners(this.activeGame.getOurTeamSummoners());
            enemyTeamListFragment.updateSummoners(this.activeGame.getEnemyTeamSummoners());
            championGuideFragment.loadUrl(getChampionGuideUrl());
        } else {
            if (isSampleMode == false) {
                if (isAvailableSession()) {
                    ActivityDelegate.openActivityWithConfirmMessage(this, LoginActivity.class, getString(R.string.can_not_find_active_game));
                } else {
                    ActivityDelegate.openActivityWithConfirmMessage(this, SummonerIdLoginActivity.class, getString(R.string.can_not_find_active_game));
                }
            } else {
                ActivityDelegate.openActivityWithConfirmMessage(this, LobbyActivity.class, getString(R.string.no_response_error));
            }
        }
    }

    private WebViewFragment.WebViewStatusChangeListener championGuideWebViewStatusChangeListener = new WebViewFragment.WebViewStatusChangeListener() {
        @Override
        public void onPageStarted(WebView webView) {
            setTitle(R.string.loading);
        }

        @Override
        public void onPageFinished(WebView webView) {
            setTitle(R.string.app_name);
        }
    };

    private void addTabContentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private void initializeTabs() {
        if (tabHost == null) {
            tabHost = (TabHost) findViewById(android.R.id.tabhost);
            tabHost.setup();
            tabHost.setOnTabChangedListener(this);

            addTab(R.string.our_summoner, R.drawable.myteam);
            addTab(R.string.enemy_summoner, R.drawable.enemies_icon);
            addTab(R.string.champion_guide, R.drawable.tips);

            ourTeamListFragment = new SummonerListFragment();
            enemyTeamListFragment = new SummonerListFragment();
            championGuideFragment = new WebViewFragment();
            championGuideFragment.setWebViewStatusChangeListener(championGuideWebViewStatusChangeListener);

            addTabContentFragment(ourTeamListFragment);
            addTabContentFragment(enemyTeamListFragment);
            addTabContentFragment(championGuideFragment);
        }

        showFragment(ourTeamListFragment);
    }

    private void showFragment(Fragment fragment) {
        if (fragment != lastFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (lastFragment != null) {
                fragmentTransaction.hide(lastFragment);
            }
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();

            lastFragment = fragment;

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

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        if (lastFragment instanceof Refreshable) {
            ((Refreshable)lastFragment).refresh();
        }
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
}

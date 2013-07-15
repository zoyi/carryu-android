package co.zoyi.carryu.Application.Views.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import co.zoyi.carryu.Application.Etc.CUCroutonStyle;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Views.Fragments.ChampionGuideFragment;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.Application.Views.Fragments.TabContentFragment;
import co.zoyi.carryu.Chat.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Chat.Services.ChatService;
import co.zoyi.carryu.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class HomeActivity extends CUActivity implements TabHost.OnTabChangeListener {
    private TabHost tabHost;

    private TabContentFragment lastFragment;
    private ChampionGuideFragment championGuideFragment;
    private SummonerListFragment ourTeamListFragment;
    private SummonerListFragment enemyTeamListFragment;

    public void onEventMainThread(ChatStatusChangeEvent event) {
        Crouton.makeText(this, event.status.toString(), CUCroutonStyle.INFO).show();

        if (event.status == ChatService.Status.CHAMPION_SELECT) {
            tabHost.getTabWidget().setVisibility(View.VISIBLE);
            showFragment(ourTeamListFragment);
        } else if (event.status == ChatService.Status.IN_GAME) {
            tabHost.getTabWidget().setVisibility(View.VISIBLE);
        } else {
            CUUtil.log(this, "Connection was closed");
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        initializeTabs();
        showFragment(ourTeamListFragment);
    }

    private void initializeTabs() {
        if (tabHost == null) {
            tabHost = (TabHost) findViewById(android.R.id.tabhost);
            tabHost.setup();
            tabHost.setOnTabChangedListener(this);
//            tabHost.getTabWidget().setVisibility(View.GONE);

            addTab(R.string.our_summoner, R.drawable.ico_star);
            addTab(R.string.enemy_summoner, R.drawable.ico_heart);
            addTab(R.string.champion_guide, R.drawable.ico_list);

            ourTeamListFragment = new SummonerListFragment();
            enemyTeamListFragment = new SummonerListFragment();
            championGuideFragment = new ChampionGuideFragment();
        }
    }

    private void showFragment(TabContentFragment fragment) {
        if (fragment != lastFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            fragment.refresh();
        }
    }

    @Override
    public void onTabChanged(String tag) {
        TabContentFragment fragment = null;

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
}

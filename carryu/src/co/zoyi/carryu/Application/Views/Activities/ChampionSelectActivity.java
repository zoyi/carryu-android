package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import co.zoyi.Chat.Listeners.FetchOurTeamNamesListener;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Events.Errors.UnknownError;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Commons.Refreshable;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChampionSelectActivity extends CUActivity implements Refreshable {
    private SummonerListFragment summonerListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_champion_activity);
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        if (event.getFragment() == getSummonerListFragment()) {
            if (Registry.getChatService().getOurTeamNames() != null) {
                updateSummonerNames(Registry.getChatService().getOurTeamNames());
            } else {
                fetchOurTeamSummonerNames();
            }
        }
    }

    private void fetchOurTeamSummonerNames() {
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() {
                    Registry.getChatService().fetchOurTeamNames(new FetchOurTeamNamesListener() {
                        @Override
                        public void onCompleted(List<String> teamNames) {
                            updateSummonerNames(teamNames);
                        }

                        @Override
                        public void onFailed() {
                            updateSummonerNames(null);
                            EventBus.getDefault().post(new UnknownError());
                        }
                    });
                }
            }, 3000
        );
    }

    private void updateSummonerNames(List<String> summonerNames) {
        if (summonerNames != null) {
            List<Summoner> summoners = new ArrayList<Summoner>();
            for (String name : summonerNames) {
                summoners.add(new Summoner(name));
            }
            getSummonerListFragment().updateSummoners(summoners);
        } else {
            getSummonerListFragment().updateSummoners(null);
        }
    }

    public SummonerListFragment getSummonerListFragment() {
        if (this.summonerListFragment == null) {
            this.summonerListFragment = (SummonerListFragment) getSupportFragmentManager().findFragmentById(R.id.summoner_list_fragment);
        }
        return this.summonerListFragment;
    }

    @Override
    public void refresh() {
        getSummonerListFragment().refresh();
    }
}

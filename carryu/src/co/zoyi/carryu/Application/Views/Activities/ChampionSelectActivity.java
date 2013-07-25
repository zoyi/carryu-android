package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import co.zoyi.Chat.Services.FetchOurTeamNamesListener;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.Errors.UnknownError;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChampionSelectActivity extends CUActivity {
    private SummonerListFragment summonerListFragment;
    private boolean isFetching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_champion_activity);
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        if (event.getFragment() == getSummonerListFragment()) {
            CUUtil.log(this, "onEventMainThread [NeedRefreshFragmentEvent]");
            if (Registry.getChatService().getOurTeamNames() != null) {
                updateSummonerNames(Registry.getChatService().getOurTeamNames());
            } else {
                fetchOurTeamSummonerNames();
            }
        }
    }

    private void fetchOurTeamSummonerNames() {
        if (isFetching == false) {
            CUUtil.log(this, "fetchOurTeamSummonerNames");
            isFetching = true;

            new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Registry.getChatService().fetchOurTeamNames(new FetchOurTeamNamesListener() {
                            @Override
                            public void onCompleted(List<String> teamNames) {
                                updateSummonerNames(teamNames);
                                isFetching = false;
                            }

                            @Override
                            public void onFailed() {
                                updateSummonerNames(null);
                                EventBus.getDefault().post(new UnknownError());
                                isFetching = false;
                            }
                        });
                    }
                }, 3000
            );
        }
    }

    private void updateSummonerNames(List<String> summonerNames) {
        if (summonerNames != null) {
            CUUtil.log(this, "updateSummonerNames # " + String.valueOf(summonerNames.size()));

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
}

package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.Chat.Services.FetchOurTeamNamesListener;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
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
    private List<Summoner> summoners = new ArrayList<Summoner>();
    private SummonerListFragment summonerListFragment;
    private boolean isFetching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_champion_activity);

        this.summonerListFragment = (SummonerListFragment) getSupportFragmentManager().findFragmentById(R.id.summoner_list_fragment);
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
                                updateSummoners(teamNames);
                                isFetching = false;
                            }

                            @Override
                            public void onFailed() {
                                updateSummoners(null);
                                EventBus.getDefault().post(new UnknownError());
                                isFetching = false;
                            }
                        });
                    }
                },
                3000
            );
        }
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        CUUtil.log(this, "onEventMainThread [NeedRefreshFragmentEvent]");
        if (Registry.getChatService().getOurTeamNames() != null) {
            updateSummoners(Registry.getChatService().getOurTeamNames());
        } else {
            fetchOurTeamSummonerNames();
        }
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        if (event.getStatus() == ChatService.Status.OUT_OF_GAME) {
            finish();
        } else if (event.getStatus() == ChatService.Status.IN_GAME) {
            ActivityDelegate.openInGameActivity(this);
        }
    }

    private void updateSummoners(List<String> summonerNames) {
        CUUtil.log(this, "updateSummoners # " + String.valueOf(summonerNames.size()));
//        numberOfUpdatedSummoner = 0;

        this.summoners.clear();
        for (String name : summonerNames) {
            this.summoners.add(new Summoner(name));
        }

        this.summonerListFragment.updateSummoners(this.summoners);

//        for (Summoner summoner : this.summoners) {
//            startFetchSummonerForUpdate(summoner);
//        }
    }
}

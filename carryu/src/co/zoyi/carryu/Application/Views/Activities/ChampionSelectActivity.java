package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.Chat.Services.FetchOurTeamNameListListener;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Events.Errors.UnknownError;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.Application.Views.Fragments.CUFragment;
import co.zoyi.carryu.Application.Views.Fragments.SummonerListFragment;
import co.zoyi.carryu.R;
import com.google.gson.Gson;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChampionSelectActivity extends CUActivity {
    static private int numberOfUpdatedSummoner = 0;
    private List<Summoner> summoners;
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
                        Registry.getChatService().fetchOurSummonerNameList(new FetchOurTeamNameListListener() {
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

//        List<String> teamNames = new ArrayList<String>();
//        teamNames.add("oH Irotion");
//        teamNames.add("goldmund");
//        teamNames.add("AllenJee");
//        updateSummoners(teamNames);
    }

    public void onEventMainThread(NeedRefreshFragmentEvent event) {
        CUUtil.log(this, "onEventMainThread [NeedRefreshFragmentEvent]");
        if (Registry.getChatService().getLastUpdatedTeamNames() != null) {
            updateSummoners(Registry.getChatService().getLastUpdatedTeamNames());
        } else {
            fetchOurTeamSummonerNames();
        }
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        if (event.getStatus() == ChatService.Status.OUT_OF_GAME) {
            finish();
        } else if (event.getStatus() == ChatService.Status.IN_GAME) {
            ActivityDelegate.openGameActivity(this);
        }
    }

    private void updateSummoners(List<String> summonerNames) {
        numberOfUpdatedSummoner = 0;
        ArrayList<Summoner> summoners = new ArrayList<Summoner>();
        if (summonerNames != null) {
            for (String id : summonerNames) {
                Summoner summoner = new Summoner(id);
                summoners.add(summoner);
                startFetchSummonerForUpdate(summoner);
            }
        }
        this.summoners = summoners;
        this.summonerListFragment.updateSummoners(summoners);
    }

    private void startFetchSummonerForUpdate(final Summoner summoner) {
        HttpRequestDelegate.fetchSummoner(summoner, new DataCallback<Summoner>() {
            @Override
            public void onSuccess(Summoner newSummoner) {
                super.onSuccess(newSummoner);
                summoner.update(newSummoner);
                numberOfUpdatedSummoner++;
                if (numberOfUpdatedSummoner == summoners.size()) {
                    summonerListFragment.updateSummoners(summoners);
                }
            }
        });
    }
}

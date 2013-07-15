package co.zoyi.carryu.Application.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Etc.HttpRequestDelegate;
import co.zoyi.carryu.Application.Events.HttpResponses.FetchSummonerEvent;
import co.zoyi.carryu.Application.Registries.CURegistry;
import co.zoyi.carryu.Chat.Events.TeamIdListUpdatedEvent;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SummonerListFragment extends TabContentFragment {
    private ListView summonerListView;
    private ArrayList<Summoner> summoners;
    private SummonerListArrayAdapter summonerListArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summoner_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        summonerListView = (ListView) view.findViewById(R.id.summoner_list);

        ArrayList<String> ourTeamIdList = new ArrayList<String>();
        ourTeamIdList.add("goldmund");
        ourTeamIdList.add("goldmund");
        ourTeamIdList.add("goldmund");
        ourTeamIdList.add("goldmund");
        ourTeamIdList.add("goldmund");
        EventBus.getDefault().post(new TeamIdListUpdatedEvent(ourTeamIdList));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void refresh() {
//        new Timer().schedule(
//            new TimerTask() {
//                @Override
//                public void run() {
//                    CURegistry.getChatService().fetchOurSummonerList();
//                }
//            }, 3000
//        );
    }

    public void onEventMainThread(TeamIdListUpdatedEvent event) {
        ArrayList<String> idList = event.getTeamIdList();

        summoners = new ArrayList<Summoner>();
        for (String id : idList) {
            Summoner summoner = new Summoner(id);
            summoners.add(summoner);
            HttpRequestDelegate.fetchSummoner(summoner);
        }

        summonerListArrayAdapter = new SummonerListArrayAdapter(getActivity(), 0, summoners);
        summonerListView.setAdapter(summonerListArrayAdapter);
    }

    public void onEventMainThread(FetchSummonerEvent event) {
        Summoner newSummoner = event.getSummoner();

        for (Summoner summoner : summoners) {
            if (newSummoner.getName().equals(summoner.getName())) {
                int originalIndex = summoners.indexOf(summoner);
                summoners.remove(summoner);
                summoners.add(originalIndex, newSummoner);
                break;
            }
        }

        summonerListArrayAdapter.notifyDataSetChanged();
    }

    private class SummonerListArrayAdapter extends ArrayAdapter<Summoner> {
        private LayoutInflater inflater;

        public SummonerListArrayAdapter(Context context, int textViewResourceId, List<Summoner> objects) {
            super(context, textViewResourceId, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.summoner_list_row, parent, false);
                CUUtil.setFontAllView((ViewGroup) convertView);
            }

            return convertView;
        }
    }
}

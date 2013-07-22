package co.zoyi.carryu.Application.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Etc.API.DataCallback;
import co.zoyi.carryu.Application.Etc.API.HttpRequestDelegate;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Views.Adapters.SummonersArrayAdapter;
import co.zoyi.carryu.R;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SummonerListFragment extends TabContentFragment {
    private ListView summonerListView;
    private List<Summoner> summoners = new ArrayList<Summoner>();
    private SummonersArrayAdapter summonersArrayAdapter;

    private View.OnClickListener reloadButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refresh();
        }
    };

    private AdapterView.OnItemClickListener summonerClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Summoner summoner = SummonersArrayAdapter.class.cast(adapterView.getAdapter()).getItem(position);
            ActivityDelegate.openSummonerDetailActivity(getActivity(), summoner.getName());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summoner_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        CUUtil.log(this, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        this.summonerListView = (ListView) view.findViewById(R.id.summoner_list);
        this.summonerListView.setOnItemClickListener(summonerClickListener);
        ImageButton.class.cast(view.findViewById(R.id.reload)).setOnClickListener(reloadButtonClickListener);

        refresh();
    }

    @Override
    public void refresh() {
        CUUtil.log(this, "refresh");
        EventBus.getDefault().post(new NeedRefreshFragmentEvent(this));
    }

    private void refreshViews() {
        if (this.summoners.size() == 0) {
            this.summonerListView.setVisibility(View.GONE);
            LinearLayout.class.cast(getView().findViewById(R.id.loading_layout)).setVisibility(View.VISIBLE);
            TextView.class.cast(getView().findViewById(R.id.loading_message)).setText(R.string.load_failed);
            ImageButton.class.cast(getView().findViewById(R.id.reload)).setVisibility(View.VISIBLE);
        } else {
            this.summonersArrayAdapter.notifyDataSetChanged();
            this.summonerListView.setVisibility(View.VISIBLE);
            LinearLayout.class.cast(getView().findViewById(R.id.loading_layout)).setVisibility(View.GONE);
        }
    }

    private Runnable createListViewAdapterAndRefreshViews = new Runnable() {
        @Override
        public void run() {
            summonersArrayAdapter = new SummonersArrayAdapter(getActivity(), 0, summoners);
            summonerListView.setAdapter(summonersArrayAdapter);
            refreshViews();
        }
    };

//    private Runnable notifyListViewAdapterDataChanged = new Runnable() {
//        @Override
//        public void run() {
//            summonersArrayAdapter.notifyDataSetChanged();
//        }
//    };

    public void updateSummoners(List<Summoner> summoners) {
        if (summoners != null) {
            CUUtil.log(this, "updateSummoners # " + String.valueOf(summoners.size()));
            this.summoners.clear();
            this.summoners.addAll(summoners);
            for (Summoner summoner : this.summoners) {
                startFetchSummonerForUpdate(summoner);
            }
        } else {
            CUUtil.log(this, "updateSummoner Failed");
        }
        getActivity().runOnUiThread(createListViewAdapterAndRefreshViews);
    }

    private void startFetchSummonerForUpdate(final Summoner summoner) {
        CUUtil.log(this, String.format("startFetchSummonerForUpdate [%s]", summoner.getName()));
        HttpRequestDelegate.fetchSummoner(summoner, new DataCallback<Summoner>() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(Summoner newSummoner) {
                CUUtil.log(this, String.format("startFetchSummonerForUpdate onSuccess [%s]", summoner.getName()));
                super.onSuccess(newSummoner);
                summoner.update(newSummoner);
                summonersArrayAdapter.notifyDataSetChanged();
            }
        });
    }

//    public void updateSummoner(Summoner newSummoner) {
//        CUUtil.log(this, String.format("updateSummoner [%s]", newSummoner.getName()));
////        summonersArrayAdapter.updateSummoner(newSummoner);
//
//        summonersArrayAdapter = new SummonersArrayAdapter(getActivity(), 0, summoners);
//        summonerListView.setAdapter(summonersArrayAdapter);
//        refreshViews();
//    }
}

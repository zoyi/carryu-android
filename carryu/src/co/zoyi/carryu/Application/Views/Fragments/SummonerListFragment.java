package co.zoyi.carryu.Application.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Datas.Serializers.SummonerJSONSerializer;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Events.NeedRefreshFragmentEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.carryu.R;
import com.loopj.android.image.SmartImageView;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummonerListFragment extends TabContentFragment {
    private ListView summonerListView;
    private List<Summoner> summoners = new ArrayList<Summoner>();
    private SummonerListArrayAdapter summonerListArrayAdapter;

    private View.OnClickListener reloadButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refresh();
        }
    };

    private AdapterView.OnItemClickListener summonerClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Summoner summoner = SummonerListArrayAdapter.class.cast(adapterView.getAdapter()).getItem(position);
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
            this.summonerListArrayAdapter.notifyDataSetChanged();
            this.summonerListView.setVisibility(View.VISIBLE);
            LinearLayout.class.cast(getView().findViewById(R.id.loading_layout)).setVisibility(View.GONE);
        }
    }

    private Runnable createListViewAdapterAndRefreshViews = new Runnable() {
        @Override
        public void run() {
            summonerListArrayAdapter = new SummonerListArrayAdapter(getActivity(), 0, summoners);
            summonerListView.setAdapter(summonerListArrayAdapter);
            refreshViews();
        }
    };

    private Runnable notifyListViewAdapterDataChanged = new Runnable() {
        @Override
        public void run() {
            summonerListArrayAdapter.notifyDataSetChanged();
        }
    };

    public void updateSummoners(List<Summoner> summoners) {
        CUUtil.log(this, "updateSummoners");
        if (summoners.size() > 0) {
            this.summoners.clear();
            this.summoners.addAll(summoners);
            getActivity().runOnUiThread(createListViewAdapterAndRefreshViews);
        }
    }

    public void updateSummoner(Summoner newSummoner) {
        CUUtil.log(this, String.format("updateSummoner [%s]", newSummoner.getName()));
        summonerListArrayAdapter.updateSummoner(newSummoner);
    }

    private class SummonerListArrayAdapter extends ArrayAdapter<Summoner> {
        private LayoutInflater layoutInflater;
        private Map<Summoner, View> rowViewMap;

        public SummonerListArrayAdapter(Context context, int textViewResourceId, List<Summoner> objects) {
            super(context, textViewResourceId, objects);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowViewMap = new HashMap<Summoner, View>();
        }

        private void updateView(View rowView, Summoner summoner) {
            CUUtil.log(String.format("updateView %s Updated: %s", SummonerJSONSerializer.getGsonInstance().toJson(summoner), String.valueOf(summoner.isUpdated())));

            TextView.class.cast(rowView.findViewById(R.id.summoner_name)).setText(summoner.getName());

            if (summoner.isUpdated() == true) {
                CUUtil.log(String.format("Update [%s]", summoner.getName()));
                SmartImageView.class.cast(rowView.findViewById(R.id.profile_image)).setImageUrl(summoner.getProfileIconUrl());
                TextView.class.cast(rowView.findViewById(R.id.summoner_level)).setText(summoner.getDisplayLevel());
                TextView.class.cast(rowView.findViewById(R.id.summoner_stat)).setText(summoner.getDisplayStats());
                SmartImageView.class.cast(rowView.findViewById(R.id.more_arrow)).setVisibility(View.VISIBLE);

                if (summoner.getRankIconUrl().length() == 0) {
                    SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setVisibility(View.GONE);
                } else {
                    SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setVisibility(View.VISIBLE);
                    SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setImageUrl(summoner.getRankIconUrl());
                }
            } else {
                CUUtil.log(String.format("Ready [%s]", summoner.getName()));
                SmartImageView.class.cast(rowView.findViewById(R.id.more_arrow)).setVisibility(View.GONE);
                TextView.class.cast(rowView.findViewById(R.id.summoner_level)).setText(getActivity().getString(R.string.loading));
            }

            rowViewMap.put(summoner, rowView);
        }

        public void updateView(Summoner summoner) {
            updateView(rowViewMap.get(summoner), summoner);
        }

        public void updateSummoner(Summoner newSummoner) {
            for (int position=0; position<getCount(); position++) {
                if (getItem(position).getName().equals(newSummoner.getName())) {
                    getItem(position).update(newSummoner);
                    updateView(getItem(position));
                    notifyDataSetChanged();
                    return;
                }
            }
            CUUtil.log(String.format("Can't find the summoner [%s]", newSummoner.getName()));
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            if (rowView == null) {
                rowView = layoutInflater.inflate(R.layout.summoner_list_row, parent, false);
                CUUtil.setFontAllView((ViewGroup) rowView);
            }

            Summoner summoner = getItem(position);
            CUUtil.log(String.format("getView [%d][%s]", position, summoner.getName()));
            updateView(rowView, summoner);

            return rowView;
        }
    }
}

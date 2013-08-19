package co.zoyi.carryu.Application.Views.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Datas.Serializers.SummonerJSONSerializer;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.R;
import com.loopj.android.image.SmartImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummonersArrayAdapter extends ArrayAdapter<Summoner> {
    private LayoutInflater layoutInflater;
    private Map<Summoner, View> rowViewMap;
    private Context context;

    public SummonersArrayAdapter(Context context, int textViewResourceId, List<Summoner> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowViewMap = new HashMap<Summoner, View>();
    }

    private void updateView(View rowView, Summoner summoner) {
        TextView.class.cast(rowView.findViewById(R.id.summoner_name)).setText(summoner.getName());

        if (summoner.isUpdated() == true) {
            if (summoner.getChampion() == null) {
                SmartImageView.class.cast(rowView.findViewById(R.id.profile_image)).setImageUrl(summoner.getProfileIconUrl());
            } else {
                SmartImageView.class.cast(rowView.findViewById(R.id.profile_image)).setImageUrl(summoner.getChampion().getChampionImageUrl());
            }

            SmartImageView.class.cast(rowView.findViewById(R.id.more_arrow)).setVisibility(View.VISIBLE);
            TextView.class.cast(rowView.findViewById(R.id.summoner_level)).setText(summoner.getDisplayLevel());
            TextView.class.cast(rowView.findViewById(R.id.summoner_stat)).setText(summoner.getDisplayStats());

            if (summoner.getRankIconUrl().length() == 0) {
                SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setVisibility(View.GONE);
            } else {
                SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setVisibility(View.VISIBLE);
                SmartImageView.class.cast(rowView.findViewById(R.id.rank_icon)).setImageUrl(summoner.getRankIconUrl());
            }

            if (summoner.getFirstSpellImageUrl().length() > 0) {
                LinearLayout.class.cast(rowView.findViewById(R.id.spell_container)).setVisibility(View.VISIBLE);
                SmartImageView.class.cast(rowView.findViewById(R.id.first_spell)).setImageUrl(summoner.getFirstSpellImageUrl());
                SmartImageView.class.cast(rowView.findViewById(R.id.second_spell)).setImageUrl(summoner.getSecondSpellImageUrl());
            } else {
                LinearLayout.class.cast(rowView.findViewById(R.id.spell_container)).setVisibility(View.GONE);
            }
        } else {
            SmartImageView.class.cast(rowView.findViewById(R.id.more_arrow)).setVisibility(View.GONE);
            TextView.class.cast(rowView.findViewById(R.id.summoner_level)).setText(context.getString(R.string.loading));
        }

        rowViewMap.put(summoner, rowView);
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.summoner_list_row, parent, false);
            CUUtil.setFontAllView((ViewGroup) rowView);
        }

        Summoner summoner = getItem(position);
        updateView(rowView, summoner);

        return rowView;
    }
}
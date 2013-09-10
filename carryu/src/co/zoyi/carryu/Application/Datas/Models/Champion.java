package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class Champion extends Model {
    @SerializedName("name")
    private String name;
    @SerializedName("ranked_stat")
    private ChampionStat rankedStat;
    @SerializedName("normal_stat")
    private ChampionStat normalStat;

    public String getChampionImageUrl() {
        return String.format("http://kr.carryu.co/champions/%d/image", getId());
    }

    public ChampionStat getRankedStat() {
        return rankedStat;
    }

    public ChampionStat getNormalStat() {
        return normalStat;
    }
}

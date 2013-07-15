package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class RankStat extends Stat {
    @SerializedName("rank")
    private int rank;
    @SerializedName("tier")
    private String tier;
    @SerializedName("losses")
    private int losses;
}

package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class ChampionStat extends Model {
    @SerializedName("score")
    private float score;

    public float getScore() {
        return score;
    }
}

package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class Stat extends Model {
    public int getWins() {
        return wins;
    }

    public float getRating() {
        return rating;
    }

    @SerializedName("wins")
    private int wins;
    @SerializedName("rating")
    private float rating;
}

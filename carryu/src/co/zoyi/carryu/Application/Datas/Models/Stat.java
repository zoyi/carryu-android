package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class Stat extends Model {
    public int getWins() {
        return wins;
    }

    @SerializedName("wins")
    private int wins;
}

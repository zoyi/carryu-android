package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class RankStat extends Stat {
    @SerializedName("rank")
    private int rank;
    @SerializedName("tier")
    private String tier;
    @SerializedName("losses")
    private int losses;

    public int getRank() {
        return rank;
    }

    public String getRomaNotationRank() {
        if (rank == 1) return "I";
        else if (rank == 2) return "II";
        else if (rank == 3) return "III";
        else if (rank == 4) return "IV";
        else if (rank == 5) return "V";

        return "";
    }

    public String getTier() {
        return tier;
    }

    public int getLosses() {
        return losses;
    }
}

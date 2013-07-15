package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class Summoner extends CUModel {
    @SerializedName("account_id")
    private int accountId;

    @SerializedName("internal_name")
    private String internalName;

    @SerializedName("level")
    private int level;

    @SerializedName("name")
    private String name;

    @SerializedName("profile_icon_id")
    private int profileIconId;
    transient private String profileIconUrl;

    @SerializedName("league_solo_5x5")
    private RankStat legueSoloFiveToFive;

    @SerializedName("player_stat_unranked")
    private Stat stat;

    public Summoner(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getProfileIconUrl() {
        return String.format("http://carryu.co/assets/profile_icons/%d.jpg", profileIconId);
    }

    public int getAccountId() {
        return accountId;
    }

    public String getInternalName() {
        return internalName;
    }

    public int getLevel() {
        return level;
    }

    public int getProfileIconId() {
        return profileIconId;
    }
}

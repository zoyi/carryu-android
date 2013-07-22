package co.zoyi.carryu.Application.Datas.Models;

import co.zoyi.carryu.Application.CUApplication;
import co.zoyi.carryu.Application.Datas.Serializers.SummonerJSONSerializer;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.R;
import com.google.gson.annotations.SerializedName;

public class Summoner extends Model {
    public static class Rooted implements Model.Rooted {
        @SerializedName("summoner")
        private Summoner summoner;

        @Override
        public Model getObject() {
            return summoner;
        }
    }

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
    @SerializedName("league_solo_5x5")
    private RankStat leagueSoloFiveToFiveStat;
    @SerializedName("player_stat_unranked")
    private Stat normalStat;
    @SerializedName("champion")
    private Champion champion;
    @SerializedName("spell1")
    private int firstSpell;
    @SerializedName("spell2")
    private int secondSpell;
    @SerializedName("is_bot")
    private boolean isBot;

    public Champion getChampion() {
        return champion;
    }

    public int getFirstSpell() {
        return firstSpell;
    }

    public int getSecondSpell() {
        return secondSpell;
    }

    transient boolean isUpdated;

    public Summoner(String name) {
        this.name = name;
        this.isUpdated = false;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public String getName() {
        return name;
    }

    public String getProfileIconUrl() {
        if (internalName == null) {
            return "";
        }
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

    public String getRankIconUrl() {
        if (leagueSoloFiveToFiveStat == null) {
            return "";
        }
        return String.format("http://carryu.co/assets/league/%s_%d.png", leagueSoloFiveToFiveStat.getTier().toLowerCase(), leagueSoloFiveToFiveStat.getRank());
    }

    public String getDisplayLevel() {
        if (leagueSoloFiveToFiveStat == null) {
            if (level == 0) {
                return "";
            }
            return String.format(CUApplication.getContext().getString(R.string.level_format), level);
        }
        return String.format("%s %s / ", leagueSoloFiveToFiveStat.getTier(), leagueSoloFiveToFiveStat.getRomaNotationRank(), level)
            + String.format(CUApplication.getContext().getString(R.string.level_format), level);
    }

    public String getDisplayStats() {
        if (leagueSoloFiveToFiveStat == null) {
            if (normalStat == null) {
                return "";
            }
            return String.format(CUApplication.getContext().getString(R.string.normal_display_stats_format), normalStat.getWins());
        }
        return String.format(CUApplication.getContext().getString(R.string.display_stats_format), normalStat.getWins(), leagueSoloFiveToFiveStat.getWins());
    }

    public void update(Summoner summoner) {
        CUUtil.log(this, "update");
        this.accountId = summoner.accountId;
        this.internalName = summoner.internalName;
        this.level = summoner.level;
        this.name = summoner.name;
        this.profileIconId = summoner.profileIconId;
        this.leagueSoloFiveToFiveStat = summoner.leagueSoloFiveToFiveStat;
        this.normalStat = summoner.normalStat;
        this.isUpdated = true;

        CUUtil.log(this, "Updated: " + SummonerJSONSerializer.getGsonInstance().toJson(this));
    }
}

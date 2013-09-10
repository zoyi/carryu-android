package co.zoyi.carryu.Application.Datas.Models;

import co.zoyi.carryu.Application.CUApplication;
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
    @SerializedName("ranked_solo_stat")
    private RankStat rankedSoloStat;
    @SerializedName("unranked_stat")
    private Stat unrankedStat;
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
        if (rankedSoloStat == null) {
            return "";
        }
        return String.format("http://carryu.co/assets/league/%s_%d.png", rankedSoloStat.getTier().toLowerCase(), rankedSoloStat.getRank());
    }

    public String getFirstSpellImageUrl() {
        if (firstSpell == 0) {
            return "";
        }
        return String.format("http://kr.carryu.co/spells/%d/image", firstSpell);
    }

    public String getSecondSpellImageUrl() {
        if (secondSpell == 0) {
            return "";
        }
        return String.format("http://kr.carryu.co/spells/%d/image", secondSpell);
    }

    public String getDisplayRankedStat() {
        if (rankedSoloStat == null) {
            if (level == 0) {
                return "";
            }
            return String.format(CUApplication.getContext().getString(R.string.level_format), level);
        }
        return String.format("%s %s", rankedSoloStat.getTier(), rankedSoloStat.getRomaNotationRank(), level);
    }

    public String getDisplayStats() {
        if (rankedSoloStat == null) {
            if (unrankedStat == null) {
                return "";
            }

            if (unrankedStat.getRating() <= 0.0) {
                return String.format(CUApplication.getContext().getString(R.string.display_unranked_stat_format), unrankedStat.getWins());
            }

            return String.format(CUApplication.getContext().getString(R.string.display_unranked_stat_with_mmr_format), unrankedStat.getWins(), unrankedStat.getRating());
        }
        if (rankedSoloStat.getRating() <= 0.0) {
            return String.format(CUApplication.getContext().getString(R.string.display_ranked_stat_format), rankedSoloStat.getWins());
        }
        return String.format(CUApplication.getContext().getString(R.string.display_ranked_stat_with_mmr_format), rankedSoloStat.getWins(), rankedSoloStat.getRating());
    }

    public String getDisplayChampionStat() {
        if (champion == null) return "";
        if (champion.getRankedStat() == null) {
            if (champion.getNormalStat() == null) {
                 return CUApplication.getContext().getString(R.string.kda_not_found);
            }
            return String.format(CUApplication.getContext().getString(R.string.kda), champion.getNormalStat().getScore());
        }
        return String.format(CUApplication.getContext().getString(R.string.kda), champion.getRankedStat().getScore());
    }

    public void update(Summoner summoner) {
        if (summoner.champion != null)  this.champion = summoner.champion;
        this.accountId = summoner.accountId;
        this.internalName = summoner.internalName;
        this.level = summoner.level;
        this.name = summoner.name;
        this.profileIconId = summoner.profileIconId;
        this.rankedSoloStat = summoner.rankedSoloStat;
        this.unrankedStat = summoner.unrankedStat;
        this.isUpdated = true;
    }
}

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
    private RankStat ranked_solo_stat;
    @SerializedName("unranked_stat")
    private Stat unranked_stat;
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
        if (ranked_solo_stat == null) {
            return "";
        }
        return String.format("http://carryu.co/assets/league/%s_%d.png", ranked_solo_stat.getTier().toLowerCase(), ranked_solo_stat.getRank());
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
        if (ranked_solo_stat == null) {
            if (level == 0) {
                return "";
            }
            return String.format(CUApplication.getContext().getString(R.string.level_format), level);
        }
        return String.format("%s %s", ranked_solo_stat.getTier(), ranked_solo_stat.getRomaNotationRank(), level);
    }

    public String getDisplayStats() {
        if (ranked_solo_stat == null) {
            if (unranked_stat == null) {
                return "";
            }

            if (unranked_stat.getRating() <= 0.0) {
                return String.format(CUApplication.getContext().getString(R.string.display_unranked_stat_format), unranked_stat.getWins());
            }

            return String.format(CUApplication.getContext().getString(R.string.display_unranked_stat_with_mmr_format), unranked_stat.getWins(), unranked_stat.getRating());
        }
        if (ranked_solo_stat.getRating() <= 0.0) {
            return String.format(CUApplication.getContext().getString(R.string.display_ranked_stat_format), ranked_solo_stat.getWins());
        }
        return String.format(CUApplication.getContext().getString(R.string.display_ranked_stat_with_mmr_format), ranked_solo_stat.getWins(), ranked_solo_stat.getRating());
    }

    public void update(Summoner summoner) {
        this.accountId = summoner.accountId;
        this.internalName = summoner.internalName;
        this.level = summoner.level;
        this.name = summoner.name;
        this.profileIconId = summoner.profileIconId;
        this.ranked_solo_stat = summoner.ranked_solo_stat;
        this.unranked_stat = summoner.unranked_stat;
        this.isUpdated = true;
    }
}

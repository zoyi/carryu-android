package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActiveGame extends Model {
    @SerializedName("player_team")
    private List<Summoner> ourTeamSummoners;
    @SerializedName("enemy_team")
    private List<Summoner> enemyTeamSummoners;

    public List<Summoner> getOurTeamSummoners() {
        return ourTeamSummoners;
    }

    public List<Summoner> getEnemyTeamSummoners() {
        return enemyTeamSummoners;
    }
}

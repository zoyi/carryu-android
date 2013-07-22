package co.zoyi.carryu.Application.Etc;

import android.content.Context;
import android.content.Intent;
import co.zoyi.carryu.Application.Views.Activities.*;

public class ActivityDelegate {
    static public void openHomeActivity(Context context) {
        context.startActivity(new Intent(context, InGameActivity.class));
    }

    static public void openLobbyActivity(Context context) {
        context.startActivity(new Intent(context, LobbyActivity.class));
    }

    static public void openLoginActivityWithConnectionClosedCrouton(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("CONNECTION_CLOSED", true);
        context.startActivity(intent);
    }

    static public void openChampionSelectActivity(Context context) {
        Intent intent = new Intent(context, ChampionSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openInGameActivity(Context context) {
        Intent intent = new Intent(context, InGameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openSummonerDetailActivity(Context context, String summonerName) {
        Intent intent = new Intent(context, SummonerDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SUMMONER_NAME", summonerName);
        context.startActivity(intent);
    }
}

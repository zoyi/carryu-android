package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import co.zoyi.carryu.Application.Views.Activities.*;

public class ActivityDelegate {
    public static boolean hasIntentExtra(Activity activity, String key) {
        Intent intent = activity.getIntent();
        return intent != null && intent.getExtras() != null && intent.getExtras().containsKey(key);
    }

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
        intent.putExtra(InGameActivity.SAMPLE_IN_GAME_INTENT_KEY, false);
        context.startActivity(intent);
    }

    static public void openSampleInGameActivity(Context context) {
        Intent intent = new Intent(context, InGameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(InGameActivity.SAMPLE_IN_GAME_INTENT_KEY, true);
        context.startActivity(intent);
    }

    static public void openSummonerDetailActivity(Context context, String summonerName) {
        Intent intent = new Intent(context, SummonerDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SUMMONER_NAME", summonerName);
        context.startActivity(intent);
    }

    static public void openSearchSummonerActivity(Context context) {
        Intent intent = new Intent(context, SearchSummonerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}

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

    public static boolean removeIntentExtra(Activity activity, String key) {
        if (hasIntentExtra(activity, key)) {
            activity.getIntent().removeExtra(key);
            return true;
        }
        return false;
    }

    static public void openLobbyActivity(Context context) {
        Intent intent = new Intent(context, LobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openActivityWithConfirmMessage(Context context, Class activityClass, String message) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(CUActivity.CONFIRM_MESSAGE_INTENT_KEY, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openLoginActivityWithConnectionClosedCrouton(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(LoginActivity.CONNECTION_CLOSED_INTENT_KEY, true);
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
        intent.putExtra(SummonerDetailActivity.SUMMONER_NAME_INTENT_KEY, summonerName);
        context.startActivity(intent);
    }

    static public void openSearchSummonerActivity(Context context) {
        Intent intent = new Intent(context, SearchSummonerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void openFeedbackActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void exitApplication(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(LoginActivity.EXIT_APPLICATION_INTENT_KEY, true);
        context.startActivity(intent);
    }
}

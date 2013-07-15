package co.zoyi.carryu.Application.Etc;

import android.content.Context;
import android.content.Intent;
import co.zoyi.carryu.Application.Views.Activities.HomeActivity;
import co.zoyi.carryu.Application.Views.Activities.LobbyActivity;
import co.zoyi.carryu.Application.Views.Activities.LoginActivity;

public class ActivityDelegate {
    static public void openHomeActivity(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    static public void openLobbyActivity(Context context) {
        context.startActivity(new Intent(context, LobbyActivity.class));
    }

    static public void openLoginActivityWithConnectionClosedCrouton(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.getExtras().putBoolean("CONNECTION_CLOSED", true);
        context.startActivity(intent);
    }
}

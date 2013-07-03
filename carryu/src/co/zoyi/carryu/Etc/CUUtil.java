package co.zoyi.carryu.Etc;

import android.util.Log;
import co.zoyi.carryu.Activities.CUActivity;

public class CUUtil {
    public static void log(CUActivity activity, String message) {
        Log.d(activity.getPackageName(), message);
    }
}

package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import co.zoyi.carryu.Application.Events.Errors.*;
import co.zoyi.carryu.R;

public class ErrorCroutonDelegate {
    public static String getErrorEventString(Context context, ErrorEvent errorEvent) {
        if (errorEvent instanceof CouldNotConnectChatServerErrorEvent) {
            return context.getString(R.string.connect_chat_server_fail_error);
        } else if (errorEvent instanceof AuthenticateFailErrorEvent) {
            return context.getString(R.string.authenticate_fail_error);
        } else if (errorEvent instanceof ConnectionClosedErrorEvent) {
            return context.getString(R.string.connection_closed_error);
        } else if (errorEvent instanceof NoResponseErrorEvent) {
            return context.getString(R.string.no_response_error);
        }

        return context.getString(R.string.unknown_error);
    }

    public static void showErrorMessage(Activity activity, ErrorEvent errorEvent) {
        String errorString = ErrorCroutonDelegate.getErrorEventString(activity, errorEvent);
        CUUtil.showCrouton(activity, errorString, CUCroutonStyle.ALERT);
    }
}

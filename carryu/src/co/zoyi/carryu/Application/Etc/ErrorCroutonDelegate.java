package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.CUErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.ConnectionClosedErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.CouldNotConnectChatServerErrorEvent;
import co.zoyi.carryu.R;

public class ErrorCroutonDelegate {
    public static String getErrorEventString(Context context, CUErrorEvent errorEvent) {
        if (errorEvent instanceof CouldNotConnectChatServerErrorEvent) {
            return context.getString(R.string.connect_chat_server_fail_error);
        } else if (errorEvent instanceof AuthenticateFailErrorEvent) {
            return context.getString(R.string.authenticate_fail_error);
        } else if (errorEvent instanceof ConnectionClosedErrorEvent) {
            return context.getString(R.string.connection_closed_error);
        }

        return context.getString(R.string.unknown_error);
    }

    public static void showErrorMessage(Activity activity, CUErrorEvent errorEvent) {
        String errorString = ErrorCroutonDelegate.getErrorEventString(activity, errorEvent);
        CUUtil.showCrouton(activity, errorString, CUCroutonStyle.ALERT);
    }
}

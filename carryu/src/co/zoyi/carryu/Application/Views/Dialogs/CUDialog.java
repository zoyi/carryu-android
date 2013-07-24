package co.zoyi.carryu.Application.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class CUDialog extends Dialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public CUDialog(Context context) {
        super(context);
    }

    public CUDialog(Context context, int theme) {
        super(context, theme);
    }

    public CUDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}

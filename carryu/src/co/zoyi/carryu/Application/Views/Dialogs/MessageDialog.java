package co.zoyi.carryu.Application.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import co.zoyi.carryu.R;

public class MessageDialog extends CUDialog {
    private String message;

    public MessageDialog(Context context, String message) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.async_progress_dialog);

        TextView.class.cast(findViewById(R.id.message)).setText(this.message);
    }

    public void setMessage(String message) {
        this.message = message;
        TextView.class.cast(findViewById(R.id.message)).setText(this.message);
    }
}

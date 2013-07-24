package co.zoyi.carryu.Application.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import co.zoyi.carryu.R;

public class AlertDialog extends CUDialog {
    private String message;

    private View.OnClickListener confirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    public AlertDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public AlertDialog(Context context, String message) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.alert_dialog);

        initializeViews();
    }

    private void initializeViews() {
        TextView.class.cast(findViewById(R.id.confirm_message)).setText(this.message);
        Button.class.cast(findViewById(R.id.confirm)).setOnClickListener(confirmClickListener);
    }
}

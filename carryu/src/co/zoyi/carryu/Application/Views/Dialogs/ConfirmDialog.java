package co.zoyi.carryu.Application.Views.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import co.zoyi.carryu.R;

public class ConfirmDialog extends CUDialog {
    private String message;
    private ConfirmListener confirmListener;

    public void setConfirmListener(ConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public static interface ConfirmListener {
        public void onConfirm();
        public void onCancel();
    }

    private View.OnClickListener confirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (confirmListener != null) {
                confirmListener.onConfirm();
            }
            dismiss();
        }
    };

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (confirmListener != null) {
                confirmListener.onCancel();
            }
            dismiss();
        }
    };

    public ConfirmDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public ConfirmDialog(Context context, String message) {
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

        setContentView(R.layout.confirm_dialog);

        initializeViews();
    }

    private void initializeViews() {
        TextView.class.cast(findViewById(R.id.confirm_message)).setText(this.message);
        Button.class.cast(findViewById(R.id.confirm)).setOnClickListener(confirmClickListener);
        Button.class.cast(findViewById(R.id.cancel)).setOnClickListener(cancelClickListener);
    }
}

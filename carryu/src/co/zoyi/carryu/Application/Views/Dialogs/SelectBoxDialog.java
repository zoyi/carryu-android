package co.zoyi.carryu.Application.Views.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.R;

import java.util.Map;


public class SelectBoxDialog extends CUDialog {
    private String message;
    private Map<Object, String> select_list;
    private int checked_index;

    private View.OnClickListener confirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };
    private RadioGroup.OnCheckedChangeListener radio_group_listener;

    public SelectBoxDialog(Context context, String message, Map<Object, String> select_list, int checked_index, RadioGroup.OnCheckedChangeListener select_listener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.message = message;
        this.select_list = select_list;
        this.radio_group_listener = select_listener;
        this.checked_index = checked_index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
    }

    private void initializeViews() {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.select_box_dialog);

        Button.class.cast(findViewById(R.id.confirm)).setOnClickListener(confirmClickListener);


        RadioGroup select_group = RadioGroup.class.cast(findViewById(R.id.select_group));
        select_group.setOnCheckedChangeListener(radio_group_listener);
        for( Map.Entry<Object, String> elem : select_list.entrySet() ){
            RadioButton radio_button = new RadioButton(this.getContext());
            radio_button.setButtonDrawable(R.drawable.ui_radio_blue);
            radio_button.setText(elem.getValue());
            radio_button.setId(ServerList.ServerInfo.ServerName.class.cast(elem.getKey()).ordinal());
            if (checked_index == ServerList.ServerInfo.ServerName.class.cast(elem.getKey()).ordinal()) {
                radio_button.setChecked(true);
            }
            select_group.addView(radio_button);
        }
        TextView.class.cast(findViewById(R.id.message)).setText(this.message);
        if (this.message.equals("")) {
            TextView.class.cast(findViewById(R.id.message)).setVisibility(View.GONE);
        }
    }

    public void setMessage(String message) {
        this.message = message;
        TextView.class.cast(findViewById(R.id.message)).setText(this.message);
    }

    public void setSelect_list(Map<Object, String> select_list) {
        this.select_list = select_list;
    }
}


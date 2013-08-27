package co.zoyi.carryu.Application.Etc;

import co.zoyi.carryu.R;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CUCroutonStyle {
    public static final Style ALERT;
    public static final Style INFO;
    public static final Style CONFIRM;

    static {
        ALERT = new Style.Builder()
            .setBackgroundColor(R.color.red)
            .setTextSize(18)
            .build();

        INFO = new Style.Builder()
            .setBackgroundColor(R.color.dark_blue)
            .setTextSize(18)
            .build();

        CONFIRM = new Style.Builder()
            .setBackgroundColor(R.color.green)
            .setTextSize(18)
            .build();
    }
}

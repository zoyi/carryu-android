package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import co.zoyi.carryu.Application.CUApplication;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CUUtil {
    public static void log(Context context, String message) {
        Log.d(context.getPackageName(), message);
    }

    public static void showCrouton(Activity activity, String text, Style style) {
        Crouton.makeText(activity, text, style).setConfiguration(
            new Configuration.Builder()
                .setDuration(1500)
                .build()
        ).show();
    }

    // Flat UI Font Utils from library
    public static Typeface regularFont, boldFont;
    public static final String BOLD_FONT_PATH = "fonts/Montserrat-Bold.ttf";
    public static final String REGULAR_FONT_PATH = "fonts/Montserrat-Regular.ttf";

    public static void loadFonts() {
        regularFont = Typeface.createFromAsset(CUApplication.getContext().getAssets(),
            CUUtil.REGULAR_FONT_PATH);
        boldFont = Typeface.createFromAsset(CUApplication.getContext().getAssets(),
            CUUtil.REGULAR_FONT_PATH);
    }

    public static void setFontAllView(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); ++i) {
            View child = vg.getChildAt(i);

            if (child instanceof ViewGroup) {

                setFontAllView((ViewGroup) child);

            } else if (child != null) {
                Typeface face;
                if (child.getTag() != null
                    && child.getTag().toString().toLowerCase()
                    .equals("bold")) {
                    face = boldFont;
                } else {
                    face = regularFont;
                }
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    textView.setTypeface(face);
                } else if (child instanceof EditText) {
                    EditText editView = (EditText) child;
                    editView.setTypeface(face);
                } else if (child instanceof RadioButton) {
                    RadioButton radioView = (RadioButton) child;
                    radioView.setTypeface(face);
                } else if (child instanceof CheckBox) {
                    CheckBox checkboxView = (CheckBox) child;
                    checkboxView.setTypeface(face);
                } else  if (child instanceof Button) {
                    Button button = (Button) child;
                    button.setTypeface(face);
                }
            }
        }
    }


}

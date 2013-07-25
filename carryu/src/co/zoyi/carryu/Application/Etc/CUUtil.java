package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import co.zoyi.carryu.Application.CUApplication;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CUUtil {
    public static void log(String message) {
        Log.d(CUApplication.getContext().getPackageName(), message);
    }

    public static void log(Activity activity, String message) {
        Log.d(activity.getPackageName() + String.format(" [%s]", activity.getClass().getSimpleName()), message);
    }
    public static void log(Fragment fragment, String message) {
        if (fragment == null) {
            log(message);
        } else if (fragment.getActivity() == null) {
            Log.d(CUApplication.getContext().getPackageName() + String.format(" [%s]", fragment.getClass().getSimpleName()), message);
        } else {
            Log.d(fragment.getActivity().getPackageName() + String.format(" [%s][%s]", fragment.getActivity().getClass().getSimpleName(), fragment.getClass().getSimpleName()), message);
        }
    }

    public static void log(Object object, String message) {
        Log.d(CUApplication.getContext().getPackageName(), String.format("[%s] %s", object.getClass().getSimpleName(), message));
    }

    public static void showCrouton(Activity activity, String text, Style style) {
        showCrouton(activity, text, style, 1500);
    }

    public static void showCrouton(Activity activity, String text, Style style, int duration) {
        Crouton.makeText(activity, text, style).setConfiguration(
            new Configuration.Builder()
                .setDuration(duration)
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
            CUUtil.BOLD_FONT_PATH);
    }

    public static void setFontAllView(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); ++i) {
            View child = vg.getChildAt(i);

            if (child instanceof ViewGroup) {
                setFontAllView((ViewGroup) child);
            } else if (child != null) {
                Typeface face;
                if (child.getTag() != null && child.getTag().toString().toLowerCase().equals("bold")) {
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

package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ViewPreferenceManager {
    public static class Builder {
        private Activity activity;
        private SharedPreferences.Editor editor;

        public Builder(Activity activity) {
            this.activity = activity;
            this.editor = activity.getSharedPreferences(activity.getClass().toString(), Context.MODE_PRIVATE).edit();
        }

        public Builder put(int id) {
            View view = activity.findViewById(id);
            String idString = String.valueOf(id);

            if (view instanceof RadioButton) {
                editor.putBoolean(idString, RadioButton.class.cast(view).isChecked());
            } else if (view instanceof EditText) {
                editor.putString(idString, EditText.class.cast(view).getText().toString());
            }

            return this;
        }

        public boolean save() {
            return editor.commit();
        }
    }

    public static void load(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getClass().toString(), Context.MODE_PRIVATE);
        Set<String> allKeys = sharedPreferences.getAll().keySet();
        for(String key : allKeys) {
            int id = Integer.parseInt(key);
            View view = activity.findViewById(id);
            if (view instanceof RadioButton) {
                RadioButton.class.cast(view).setChecked(sharedPreferences.getBoolean(key, false));
            } else if (view instanceof EditText) {
                EditText.class.cast(view).setText(sharedPreferences.getString(key, ""));
            }
        }
    }
}

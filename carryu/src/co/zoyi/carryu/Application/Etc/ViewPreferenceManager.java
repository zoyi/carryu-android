package co.zoyi.carryu.Application.Etc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class ViewPreferenceManager {
    public static class Storage {
        private Activity activity;

        public Storage(Activity activity) {
            this.activity = activity;
        }

        public Storage save(int id, String key) {
            SharedPreferences.Editor editor = activity.getSharedPreferences(activity.getClass().toString(), Context.MODE_PRIVATE).edit();

            View view = activity.findViewById(id);
            if (view instanceof RadioButton) {
                editor.putBoolean(key, RadioButton.class.cast(view).isChecked());
            } else if (view instanceof EditText) {
                editor.putString(key, EditText.class.cast(view).getText().toString());
            }

            editor.commit();

            return this;
        }
    }

    public static class Loader {
        private Activity activity;

        public Loader(Activity activity) {
            this.activity = activity;
        }

        public Loader load(int id, String key) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getClass().toString(), Context.MODE_PRIVATE);

            if (sharedPreferences.contains(key)) {
                View view = activity.findViewById(id);
                if (view instanceof RadioButton) {
                    RadioButton.class.cast(view).setChecked(sharedPreferences.getBoolean(key, false));
                } else if (view instanceof EditText) {
                    EditText.class.cast(view).setText(sharedPreferences.getString(key, ""));
                }
            }

            return this;
        }

    }

//    public static void load(Activity activity) {
//        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getClass().toString(), Context.MODE_PRIVATE);
//        Set<String> allKeys = sharedPreferences.getAll().keySet();
//        for(String key : allKeys) {
//            int id = Integer.parseInt(key);
//            View view = activity.findViewById(id);
//            if (view instanceof RadioButton) {
//                RadioButton.class.cast(view).setChecked(sharedPreferences.getBoolean(key, false));
//            } else if (view instanceof EditText) {
//                EditText.class.cast(view).setText(sharedPreferences.getString(key, ""));
//            }
//        }
//    }
}

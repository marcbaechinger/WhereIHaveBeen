package ch.marcbaechinger.whereihavebeen.app;

import android.content.Context;
import android.content.SharedPreferences;

import app.ch.marcbaechinger.whereihavebeen.R;

public class PreferenceManager {
    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public boolean writeStringProperty(int resKey, String value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(resKey), value);
        return editor.commit();
    }
    public String getStringProperty(int resKey, String defValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        return prefs.getString(context.getString(resKey), defValue);
    }
}

package pcloud.task.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "APP_SETTINGS";
    private static final String ACCESS_TOKEN = "access_token";


    private SharedPreferencesManager() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getAccessToken(Context context) {
        return getSharedPreferences(context).getString(ACCESS_TOKEN, null);
    }

    public static void setAccessToken(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ACCESS_TOKEN, newValue);
        editor.commit();
    }
}
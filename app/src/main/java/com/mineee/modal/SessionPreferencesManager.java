package com.mineee.modal;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by keerthick on 5/28/2015.
 */
public class SessionPreferencesManager  {
    private static final String APP_SETTINGS = "APP_SETTINGS";


    // properties
    private static final String LoggedUserID = "LOGGED_USER_ID";
    // other properties...


    private SessionPreferencesManager() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean contains(Context context,String key ) {
        return getSharedPreferences(context).contains(key);
    }
    public static String getLoggedUserID(Context context) {
        return getSharedPreferences(context).getString(LoggedUserID, null);
    }

    public static void setLoggedUserID(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LoggedUserID , newValue);
        editor.commit();
    }
}

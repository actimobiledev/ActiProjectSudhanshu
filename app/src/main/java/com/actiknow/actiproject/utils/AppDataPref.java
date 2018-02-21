package com.actiknow.actiproject.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class AppDataPref {
    public static String HOME_FEATURED = "home_featured";
    public static String HOME_EVENTS = "home_events";
    public static String HOME_CONTACTS = "home_contacts";
    public static String HOME_SERVICE = "home_service";
    public static String HOME_ACCOUNT = "home_account";
    private static AppDataPref appDataPref;
    private String APP_DATA = "APP_DATA";
    
    public static AppDataPref getInstance () {
        if (appDataPref == null)
            appDataPref = new AppDataPref ();
        return appDataPref;
    }
    
    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (APP_DATA, Context.MODE_PRIVATE);
    }
    
    public String getStringPref (Context context, String key) {
        return getPref (context).getString (key, "");
    }
    
    public int getIntPref (Context context, String key) {
        return getPref (context).getInt (key, 0);
    }
    
    public boolean getBooleanPref (Context context, String key) {
        return getPref (context).getBoolean (key, false);
    }
    
    public void putBooleanPref (Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putBoolean (key, value);
        editor.apply ();
    }
    
    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
    
        editor.apply ();
    }
    
    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }
}

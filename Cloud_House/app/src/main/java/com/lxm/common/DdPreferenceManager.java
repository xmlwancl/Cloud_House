package com.lxm.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.lxm.DdApplication;

public class DdPreferenceManager {

    private final String FILE_PREFS = ".dd_pref";

    private SharedPreferences preferences;

    public static DdPreferenceManager getIntance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static final DdPreferenceManager instance = new DdPreferenceManager();
    }

    private DdPreferenceManager() {
        preferences = DdApplication.app.getSharedPreferences(FILE_PREFS, Context.MODE_PRIVATE);
    }

    public void savePrefString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getPrefString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public void savePrefBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getPrefBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }
}

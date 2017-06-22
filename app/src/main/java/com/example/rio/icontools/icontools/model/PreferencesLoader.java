package com.example.rio.icontools.icontools.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rio on 17-6-21.
 * 这个类主要用来保存每个包名对应的图标版本号
 */

public class PreferencesLoader {
    private static final String ICON_VERSION_PREF = "icon_versions";
    public static int DEFAULT_VALUE = -1;

    private SharedPreferences mVersionPref;
    private Context mContext;


    public PreferencesLoader(Context context) {
        mContext = context;
        mVersionPref = mContext.getSharedPreferences(ICON_VERSION_PREF, Context.MODE_PRIVATE);
    }


    public int getVersion(String key) {
        return mVersionPref.getInt(key, DEFAULT_VALUE);
    }

    public void updateVersion(String key, int value) {
        SharedPreferences.Editor editor = mVersionPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }


}


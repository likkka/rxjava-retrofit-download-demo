package com.example.rio.icontools.icontools.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rio on 17-6-21.
 * 这个类主要用来保存每个包名对应的图标版本号
 */

public class PreferencesLoader {
    private static final String ICON_VERSION_PREF = "icon_versions";
    public static long DEFAULT_VALUE = -1;

    private SharedPreferences mVersionPref;
    private Context mContext;


    public PreferencesLoader(Context context) {
        mContext = context;
        mVersionPref = mContext.getSharedPreferences(ICON_VERSION_PREF, Context.MODE_PRIVATE);
    }


    public long getVersion(String key) {
        return mVersionPref.getLong(key, DEFAULT_VALUE);
    }

    public void updateVersion(String key, long value) {
        SharedPreferences.Editor editor = mVersionPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }


}


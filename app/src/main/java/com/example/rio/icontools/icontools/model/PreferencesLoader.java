package com.example.rio.icontools.icontools.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rio on 17-6-21.
 */

public class PreferencesLoader {
    public static int DEFAULT_VALUE = -1;

    private SharedPreferences mSharedPreferences;
    private Context mContext;


    public PreferencesLoader(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public int getInt(String key) {
        return mSharedPreferences.getInt(key, DEFAULT_VALUE);
    }


    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}


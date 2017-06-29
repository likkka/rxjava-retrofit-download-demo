package com.example.rio.icontools.icontools.controller;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by rio on 17-6-29.
 */

public class NotifyIconChangeHelper {
    public static final String ACTION_ICONCHANGE = "com.meizu.flyme.launcher.CHANGEICON";
    private static final String CHANGETYPE = "changeType";
    private static final int TYPE_ICON = 0;
    private static final int TYPE_TITLE = 1;
    private final int ITEM_TYPE_APPLICATION = 0;
    public static final String ITEMTYPE = "itemType";
    public static final String ICONPACKAGE = "iconPackage";
    public static final int DEFAULT_ERROR_CODE = 100;

    public void sendNotify(Context mContext, ArrayList<String> strs) {
        Intent intent = new Intent(ACTION_ICONCHANGE);
        intent.putStringArrayListExtra(ICONPACKAGE, strs);
        intent.putExtra(CHANGETYPE, TYPE_ICON);
        intent.putExtra(ITEMTYPE, ITEM_TYPE_APPLICATION);

        mContext.sendBroadcast(intent);
    }
}

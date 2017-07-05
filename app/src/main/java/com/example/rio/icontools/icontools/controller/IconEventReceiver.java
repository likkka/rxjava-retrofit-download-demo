package com.example.rio.icontools.icontools.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.rio.icontools.icontools.IconManager;
import com.example.rio.icontools.icontools.utils.IconUtils;

/**
 * Created by huangminzhi on 17-6-20.
 */

public class IconEventReceiver extends BroadcastReceiver {
    private static final String KEY_PATH = "pre_install_path";
    public static String ACTION_ICONPULL = "com.flyme.iconevent.pull";
    public static String ACTION_ICONCHECK = "com.flyme.iconevent.check";
    String KEY_PKGNAME = "package";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_ICONPULL)) {
            String path = intent.getStringExtra(KEY_PATH);
            String pkgName = IconUtils.parseApkPackageName(context, path);
            if (pkgName == null) return;
            IconManager.getInstance().pullIcon(context, pkgName, true);
        } else if (TextUtils.equals(action, ACTION_ICONCHECK)) {
            IconManager.getInstance().checkIcons(context, false);
        } else if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            IconManager.getInstance().setScheduleCheck(context);
        }
    }
}

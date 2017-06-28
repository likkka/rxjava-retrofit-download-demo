package com.example.rio.icontools.icontools.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.rio.icontools.icontools.IconManager;

/**
 * Created by huangminzhi on 17-6-20.
 */

public class IconEventReceiver extends BroadcastReceiver {
    public static String ACTION_ICONPULL = "com.flyme.iconevent.pull";
    public static String ACTION_ICONCHECK = "com.flyme.iconevent.check";
    String KEY_PKGNAME = "package";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null || intent.getAction() == null) {
            return;
        }
        String pkgName = intent.getStringExtra(KEY_PKGNAME);

        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_ICONPULL)) {
            if (pkgName == null) {
                pkgName = "com.tencent.mm";
            }
            IconManager.getInstance().pullIcon(context, pkgName);
        } else if (TextUtils.equals(action, ACTION_ICONCHECK)) {
            IconManager.getInstance().checkIcons(context);
        } else if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            IconManager.getInstance().setScheduleCheck(context);
        }
    }
}

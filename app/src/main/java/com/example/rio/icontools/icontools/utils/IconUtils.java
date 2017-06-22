package com.example.rio.icontools.icontools.utils;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by rio on 17-6-21.
 */

public class IconUtils {
    //根据机型获取dpi
    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }
    public static void saveBitmap(int type, String name, Bitmap bitmap) {

    }
}

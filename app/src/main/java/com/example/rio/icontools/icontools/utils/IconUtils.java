package com.example.rio.icontools.icontools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.rio.icontools.icontools.bean.FlymeIconBean;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rio on 17-6-21.
 */

public class IconUtils {
    public static final String APP_ID = "909be9ff-1f9e-4eda-842f-7bb7b922e855";
    public static final String APP_KEY = "de14a520d8c910668bf177e60eee650d";
    public static final int TYPE_LAUNCHER = 0;
    public static final int TYPE_STATUS = 1;
    protected static MessageDigest messagedigest = null;
    public static final String TAG = "IconUtils";
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };


    //根据机型获取dpi
    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }
    public static void saveBitmap(int type, String name, Bitmap bitmap) {

    }

    public static String makeAppKey() {
        long timeStamp = System.currentTimeMillis();
        return md5(timeStamp + APP_KEY) + "," + timeStamp ;
    }

    public static String MD5Encode(String origin, String encoding) {
        String resultString = null;

        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (encoding == null) {
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(encoding)));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resultString;
    }

    private static String md5(String s) {
        String resultString = null;
        try {
            resultString = new String(s);
            MessageDigest md = MessageDigest.getInstance("MD5");
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
        } catch (Exception e) {
            Log.e(TAG, "calculate md5 failed");
        }
        return resultString;
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i], true));
        }
        return resultSb.toString();
    }


    private static String byteToHexString(byte b, boolean bigEnding) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return (bigEnding)?(hexDigits[d1] + hexDigits[d2]):(hexDigits[d2] + hexDigits[d1]);
    }


    public static String getUrl(FlymeIconBean bean, int dpi, int type) {
        if (dpi < DisplayMetrics.DENSITY_XXHIGH) {
            return type == TYPE_LAUNCHER ? bean.getIconS() : bean.getsIconS();
        } else if (dpi > DisplayMetrics.DENSITY_XXHIGH) {
            return type == TYPE_LAUNCHER ? bean.getIconL() : bean.getsIconL();
        } else {
            return type == TYPE_LAUNCHER ? bean.getIconM() : bean.getsIconM();
        }
    }
}

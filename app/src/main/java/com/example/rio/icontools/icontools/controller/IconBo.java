package com.example.rio.icontools.icontools.controller;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by rio on 17-6-20.
 */

public interface IconBo {
    void pullIcon(Context context, String data);
    void updateIcons(Context context, ArrayList<String> data);
    void checkIcons(Context context);
}

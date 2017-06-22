package com.example.rio.icontools.icontools.controller;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by huangminzhi on 17-6-20.
 * 客户端主要逻辑接口
 */

public interface IconBo {
    void pullIcon(Context context, String data);
    void updateIcons(Context context, ArrayList<String> data);
    void checkIcons(Context context);
    void setScheduleCheck(Context context);
}

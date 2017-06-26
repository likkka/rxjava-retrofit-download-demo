package com.example.rio.icontools.icontools.bean;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by huangminzhi on 17-6-21.
 * 保存下载内容的数据结构
 */

public class DownloadBean {
     public String pkgName;
     public long updateAt;
     public Bitmap launcherIcon;
     public Bitmap statusIcon;
     public boolean isValid;
     public InputStream lIs;
     public InputStream sIs;
}

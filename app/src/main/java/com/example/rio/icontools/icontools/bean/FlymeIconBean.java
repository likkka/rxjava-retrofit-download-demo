package com.example.rio.icontools.icontools.bean;

/**
 * Created by huangminzhi on 17-6-20.
 */

public class FlymeIconBean {
    private String mPkgName;
    private int mLauncherIconVersion;
    private int mStatusBarIconVersion;
    private String mLauncherIconURL;
    private String mStatusIconURL;

    public String getmLauncherIconURL() {
        return mLauncherIconURL;
    }

    public void setmLauncherIconURL(String mLauncherIconURL) {
        this.mLauncherIconURL = mLauncherIconURL;
    }

    public String getmStatusIconURL() {
        return mStatusIconURL;
    }

    public void setmStatusIconURL(String mStatusIconURL) {
        this.mStatusIconURL = mStatusIconURL;
    }

    public int getmLauncherIconVersion() {
        return mLauncherIconVersion;
    }

    public void setmLauncherIconVersion(int mLauncherIconVersion) {
        this.mLauncherIconVersion = mLauncherIconVersion;
    }

    public String getmPkgName() {
        return mPkgName;
    }

    public void setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public int getmStatusBarIconVersion() {
        return mStatusBarIconVersion;
    }

    public void setmStatusBarIconVersion(int mStatusBarIconVersion) {
        this.mStatusBarIconVersion = mStatusBarIconVersion;
    }

    @Override
    public String toString() {
        return "FlymeIconBean{" +
                "mLauncherIconURL='" + mLauncherIconURL + '\'' +
                ", mLauncherIconVersion=" + mLauncherIconVersion +
                ", mStatusBarIconVersion=" + mStatusBarIconVersion +
                ", mStatusIconURL='" + mStatusIconURL + '\'' +
                ", mPkgName='" + mPkgName + '\'' +
                '}';
    }
}

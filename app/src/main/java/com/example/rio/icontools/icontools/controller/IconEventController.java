package com.example.rio.icontools.icontools.controller;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.rio.icontools.icontools.bean.DownloadBean;
import com.example.rio.icontools.icontools.bean.FlymeIconBean;
import com.example.rio.icontools.icontools.download.IconRetrofit;
import com.example.rio.icontools.icontools.utils.IconUtils;
import com.example.rio.icontools.icontools.download.ServerApi;
import com.example.rio.icontools.icontools.model.PreferencesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by huangminzhi on 17-6-20.
 *
 */

public class IconEventController implements IconBo{
    private Context mContext = null;
    private static volatile int DPI = -1;
    PreferencesLoader prefLoader = null;
    int TYPE_LAUNCHER = 0;
    int TYPE_STATUS = 1;

    private IconEventController() {
    }
    public static IconEventController getInstance() {
        return SingletonHolder.INSTANCE;
    }
    private static class SingletonHolder {
        static IconEventController INSTANCE = new IconEventController();
    }

    @Override
    public void pullIcon(Context context, final String data) {
        mContext = context;
        if (DPI == -1) {
            DPI = IconUtils.getDpi(context);
        }
        Observable
                .just(data)
                .subscribeOn(Schedulers.io())
                .map(str2beanF)
                .filter(ifBeanValidF)
                .map(bean2BitmapsF)
                .filter(ifBitmapValiedF)
                .doOnNext(saveDownloadBitmapn)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(finishA);
    }

    private synchronized void updateVersion(DownloadBean downloadBean) {
        if (prefLoader == null && mContext == null) {
            return;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }
        if (downloadBean.launcherIcon != null) {
            prefLoader.saveInt(downloadBean.pkgName + TYPE_LAUNCHER, downloadBean.launcherIconVersion);
        }
        if (downloadBean.statusIcon != null) {
            prefLoader.saveInt(downloadBean.pkgName + TYPE_STATUS, downloadBean.statusBarIconVersion);
        }
    }

    private synchronized int getVersion(String data, int type) {
        if (prefLoader == null && mContext == null) {
            return PreferencesLoader.DEFAULT_VALUE;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }

        return prefLoader.getInt(data + type);
    }

    @Override
    public void updateIcons(Context context, ArrayList<String> data) {

    }

    @Override
    public void checkIcons(Context context) {

    }

    private void notifyChange() {
        //todo notify launcher to reget icon
    }

    private InputStream getUrlStream(String url) {
        try {
            return getServerSingleton().getIcon(url).execute().body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Func1<String, Call<FlymeIconBean>> str2beanF = new Func1<String, Call<FlymeIconBean>>() {
        @Override
        public Call<FlymeIconBean> call(String s) {
            try {
                return getServerSingleton().fetchInfos(s, DPI);
            } catch (Exception e) {
                //todo 待下载队列
                e.printStackTrace();
                return null;
            }
        }
    };


    private Func1<Call<FlymeIconBean>, DownloadBean> bean2BitmapsF = new Func1<Call<FlymeIconBean>, DownloadBean>() {
        @Override
        public DownloadBean call(Call<FlymeIconBean> flymeIconBeanCall) {
            DownloadBean bitmapBean = new DownloadBean();
            try {
                FlymeIconBean bean = flymeIconBeanCall.execute().body();
                int lVer = getVersion(bean.getmPkgName(), TYPE_LAUNCHER);
                int sVer = getVersion(bean.getmPkgName(), TYPE_STATUS);
                int serverLver = bean.getmLauncherIconVersion();
                int serverSver = bean.getmStatusBarIconVersion();

                if (serverLver > lVer) {
                    String url = bean.getmStatusIconURL();
                    if(url != null) {
                        bitmapBean.launcherIcon = BitmapFactory.decodeStream(getUrlStream(url));
                        bitmapBean.launcherIconVersion = serverLver;
                        bitmapBean.isValid = true;
                    }
                }
                if (serverLver > sVer) {
                    String url = bean.getmStatusIconURL();
                    if(url != null) {
                        bitmapBean.statusIcon = BitmapFactory.decodeStream(getUrlStream(url));
                        bitmapBean.statusBarIconVersion = serverSver;
                        bitmapBean.isValid = true;

                    }
                }
                bitmapBean.pkgName = bean.getmPkgName();
            } catch (Exception e) {
                //todo 待下载队列
                e.printStackTrace();
            }
            return bitmapBean;
        }
    };

    private Func1<DownloadBean, Boolean> ifBitmapValiedF = new Func1<DownloadBean, Boolean>() {
        @Override
        public Boolean call(DownloadBean downloadBean) {
            return downloadBean.isValid;
        }
    };

    private Func1<Call<FlymeIconBean>, Boolean> ifBeanValidF = new Func1<Call<FlymeIconBean>, Boolean>() {
        @Override
        public Boolean call(Call<FlymeIconBean> bean) {
            return bean != null;
        }
    };

    private Action1<DownloadBean> saveDownloadBitmapn = new Action1<DownloadBean>() {
        @Override
        public void call(DownloadBean download) {
            IconUtils.saveBitmap(0, download.pkgName, download.launcherIcon);
            IconUtils.saveBitmap(1, download.pkgName, download.statusIcon);
        }
    };

    Action1<DownloadBean> finishA = new Action1<DownloadBean>() {
        @Override
        public void call(DownloadBean downloadBean) {
            updateVersion(downloadBean);
            notifyChange();
            updateFlymeIconTheme();
        }
    };


    private void updateFlymeIconTheme() {
        try {
            //更新configuration，以便清除主题数据，各个应用会重走生命周期，其效果类似于，在设置里变更字体大小或变更语言时一样
//            IActivityManager am = ActivityManagerNative.getDefault();
//            Configuration config = am.getConfiguration();
//            config.configurationExt.fireThemeChange();
//            am.updateConfiguration(config);
        } catch (Exception e) {
            //...
        }
    }




    protected static final Object monitor = new Object();
    static ServerApi sServierSingleton = null;

    /**
     * 获取服务器接口
     * @return
     */
    public static ServerApi getServerSingleton() {
        synchronized (monitor) {
            if (sServierSingleton == null) {
                sServierSingleton = new IconRetrofit().getServerService();
            }
            return sServierSingleton;
        }
    }
}

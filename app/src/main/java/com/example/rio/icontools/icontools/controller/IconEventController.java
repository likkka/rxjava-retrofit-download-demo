package com.example.rio.icontools.icontools.controller;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

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
 * 客户端的控制中心，
 * 主要业务
 * 1.下载单个图标
 * 2.查询所有图标是否需要更新
 * 3.图标版本号数据的保存和更新
 * 4.下载失败定时再重新下载的处理
 */

public class IconEventController implements IconBo{
    private static final long DEFAULT_INTERVAL_CHECK_TIME = 1000 * 60 * 60 * 24 * 3;
    private static final long INTERVAL_CHECK_TIME = DEFAULT_INTERVAL_CHECK_TIME;
    private static final long INTERVAL_REDOWNLOAD_TIME = 1000* 60 *3;
    public static final int JOB_INTERVAL_CHECK = 1;
    public static final int JOB_RESTART_DOWNLOAD = 2;
    public static final String KEY_PACKAGE = "package";
    private Context mContext = null;
    private static volatile int DPI = -1;
    PreferencesLoader prefLoader = null;
    JobScheduler jobScheduler = null;
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

    @Override
    public void updateIcons(Context context, ArrayList<String> data) {

    }

    @Override
    public void checkIcons(Context context) {

    }

    @Override
    public void setScheduleCheck(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        startScheduler(JOB_INTERVAL_CHECK, null);
    }

    /**
     * 更新图标版本号
     * @param downloadBean
     */
    private synchronized void updateVersion(DownloadBean downloadBean) {
        if (prefLoader == null && mContext == null) {
            return;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }
        if (downloadBean.launcherIcon != null) {
            prefLoader.updateVersion(downloadBean.pkgName + TYPE_LAUNCHER, downloadBean.launcherIconVersion);
        }
        if (downloadBean.statusIcon != null) {
            prefLoader.updateVersion(downloadBean.pkgName + TYPE_STATUS, downloadBean.statusBarIconVersion);
        }
    }

    /**
     * 获取图标版本号
     * @param data
     * @param type
     * @return
     */
    private synchronized int getVersion(String data, int type) {
        if (prefLoader == null && mContext == null) {
            return PreferencesLoader.DEFAULT_VALUE;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }

        return prefLoader.getVersion(data + type);
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

    private Func1<String, FlymeIconBean> str2beanF = new Func1<String, FlymeIconBean>() {
        @Override
        public FlymeIconBean call(String s) {
            try {
                Call<FlymeIconBean> callBean = getServerSingleton().fetchInfos(s, DPI);
                return callBean.execute().body();
            } catch (Exception e) {
                startScheduler(JOB_RESTART_DOWNLOAD, s);
                e.printStackTrace();
                return null;
            }
        }
    };


    private Func1<FlymeIconBean, DownloadBean> bean2BitmapsF = new Func1<FlymeIconBean, DownloadBean>() {
        @Override
        public DownloadBean call(FlymeIconBean bean) {
            DownloadBean bitmapBean = new DownloadBean();

            try {
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
                startScheduler(JOB_RESTART_DOWNLOAD, bean.getmPkgName());
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

    private Func1<FlymeIconBean, Boolean> ifBeanValidF = new Func1<FlymeIconBean, Boolean>() {
        @Override
        public Boolean call(FlymeIconBean bean) {
            return bean != null;
        }
    };

    private Action1<DownloadBean> saveDownloadBitmapn = new Action1<DownloadBean>() {
        @Override
        public void call(DownloadBean download) {
            IconUtils.saveBitmap(TYPE_LAUNCHER, download.pkgName, download.launcherIcon);
            IconUtils.saveBitmap(TYPE_STATUS, download.pkgName, download.statusIcon);
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

    private JobScheduler getScheduler() {
        if (jobScheduler != null) {
            return jobScheduler;
        }
        if (mContext == null) {
            return null;
        }
        jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        return jobScheduler;
    }

    private void startScheduler(int jobId, @Nullable String pkg) {
        JobScheduler scheduler = getScheduler();
        if (scheduler == null) {
            return;
        }
        ComponentName cn = new ComponentName(mContext.getPackageName(), ScheduleService.class.getName());
        JobInfo.Builder builder = null;
        switch (jobId) {
            case JOB_INTERVAL_CHECK:
                builder = new JobInfo.Builder(JOB_INTERVAL_CHECK, cn);
                builder.setPeriodic(INTERVAL_CHECK_TIME);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                builder.setPersisted(false);
                break;

            case JOB_RESTART_DOWNLOAD:
                if (pkg == null) return;
                builder = new JobInfo.Builder(JOB_RESTART_DOWNLOAD, cn);
                builder.setPeriodic(INTERVAL_REDOWNLOAD_TIME);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                builder.setPersisted(true);
                PersistableBundle bundle = new PersistableBundle();
                bundle.putString(KEY_PACKAGE, pkg);
                builder.setExtras(bundle);
                break;
            default:
                break;
        }

        if (builder == null) return;
        scheduler.schedule(builder.build());
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

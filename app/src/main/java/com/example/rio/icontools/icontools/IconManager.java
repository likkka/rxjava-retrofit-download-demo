package com.example.rio.icontools.icontools;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.rio.icontools.icontools.bean.BaseEntity;
import com.example.rio.icontools.icontools.bean.DownloadBean;
import com.example.rio.icontools.icontools.bean.FlymeIconBean;
import com.example.rio.icontools.icontools.download.FetchUrlFailedException;
import com.example.rio.icontools.icontools.download.GetInputStreamFailedException;
import com.example.rio.icontools.icontools.download.IconRetrofit;
import com.example.rio.icontools.icontools.utils.IconUtils;
import com.example.rio.icontools.icontools.download.ServerApi;
import com.example.rio.icontools.icontools.model.PreferencesLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
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

public class IconManager implements IconEvent{
    private static final long DEFAULT_INTERVAL_CHECK_TIME = 1000 * 60 * 60 * 24 * 3;
    private static final long INTERVAL_CHECK_TIME = DEFAULT_INTERVAL_CHECK_TIME;
    private static final long INTERVAL_REDOWNLOAD_TIME = 1000* 60 *3;
    public static final int JOB_INTERVAL_CHECK = 1;
    public static final int JOB_RESTART_DOWNLOAD = 2;
    public static final String KEY_PACKAGE = "packageName";
    private String KEY_IN = "$in";
    private static final String TAG = "IconEventController";
    private Context mContext = null;
    private static volatile int DPI = -1;
    PreferencesLoader prefLoader = null;
    JobScheduler jobScheduler = null;
    public static boolean DEBUG = true;

    private IconManager() {
    }
    public static IconManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    private static class SingletonHolder {
        static IconManager INSTANCE = new IconManager();
    }

    @Override
    public void pullIcon(Context context, final String data, boolean retry) {

        mContext = context;
        if (DPI == -1) {
            DPI = IconUtils.getDpi(context);
        }
        if (DEBUG) Log.i(TAG, "start pull icons");
        Subscriber<List<DownloadBean>> onFinishAction = null;
        if(!retry) {
            onFinishAction = onFinishNetWorkNotRetry;
        } else {
            onFinishAction = onFinishNetWork;
        }
        Observable
                .just(data)
                .subscribeOn(Schedulers.io())
                .map(str2whereQueryStr)
                .compose(this.<String>doNetWork())
                .subscribe(onFinishAction);
    }

    @Override
    public void checkIcons(final Context context, boolean retry) {
        if (context == null) {
            return;
        }
        if (mContext == null) {
            mContext = context;
        }
        if (DEBUG) Log.i(TAG, "start checkIcons");
        recordTimestamp(mContext);
        Subscriber<List<DownloadBean>> onFinishAction = null;
        if(!retry) {
            onFinishAction = onFinishNetWorkNotRetry;
        } else {
            onFinishAction = onFinishNetWork;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        Observable.just(resolveInfoList)
                .map(resolve2strsF)
                .subscribeOn(Schedulers.io())
                .filter(ifListValiedF)
                .map(strs2whereQueryStr)
                .compose(this.<String>doNetWork())
                .subscribe(onFinishAction);

    }

    private synchronized void recordTimestamp(Context mContext) {
        if (prefLoader == null && mContext == null) {
            return;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }
        long timestamp = System.currentTimeMillis();
        if (DEBUG) Log.i(TAG, "record timestamp: " + timestamp);
        prefLoader.updateCheckTimestamp(timestamp);
    }


    /**
     * 整合网络下载事件
     * @return
     */
    private Observable.Transformer<String, List<DownloadBean>> doNetWork() {
        return new Observable.Transformer<String, List<DownloadBean>>() {
            @Override
            public Observable<List<DownloadBean>> call(Observable<String> tObservable) {
                return tObservable
                        .map(queryStr2FlymeBeanF)
                .map(flymeBean2DownloadBeansF)
                .filter(ifListValiedF)
                .doOnNext(saveDownloadBitmapA)
                .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 获取包名列表
     */
    Func1<List<ResolveInfo>, List<String>> resolve2strsF =  new Func1<List<ResolveInfo>, List<String>>() {
        @Override
        public List<String> call(List<ResolveInfo> resolveInfoList) {
            ArrayList<String> result = new ArrayList<>();
            for(ResolveInfo re : resolveInfoList) {
                if (result.contains(re.activityInfo.packageName)) continue;
                if ((re.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)  continue;
                if ((re.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)  continue;
                result.add(re.activityInfo.packageName);
            }
            return result;
        }
    };

    @Override
    public void setScheduleCheck(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        startScheduler(JOB_INTERVAL_CHECK, null);
    }

    /**
     * 多个包名查询，创建query value
     */
    Func1<List<String>, String> strs2whereQueryStr = new Func1<List<String>, String>() {
        @Override
        public String call(List<String> strings) {
            strings.add("com.tencent.mm");// 和server 模拟数据
            JsonObject wrapper0 = new JsonObject();
            JsonObject wrapper1 = new JsonObject();
            JsonArray ja = new JsonArray();
            for (String s : strings) ja.add(s);
            wrapper1.add(KEY_IN, ja);
            wrapper0.add(KEY_PACKAGE, wrapper1);
            String where = wrapper0.toString();
            return where;
        }
    };

    /**
     * 单个包名查询，创建query value
     */
    Func1<String, String> str2whereQueryStr = new Func1<String, String>() {
        @Override
        public String call(String s) {
            JsonObject jo = new JsonObject();
            jo.addProperty(KEY_PACKAGE, s);
            return jo.toString();
        }
    };
    
    
    /**
     * 更新图标版本号
     * @param downloadBeans
     */
    private synchronized void updateVersion(List<DownloadBean> downloadBeans) {
        if (prefLoader == null && mContext == null) {
            return;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }
        for (DownloadBean downloadBean : downloadBeans) {
            prefLoader.updateVersion(downloadBean.pkgName, downloadBean.updateAt);
            if(DEBUG) Log.i(TAG, String.format("update %s : %d", downloadBean.pkgName, downloadBean.updateAt));
        }
    }

    /**
     * 获取图标版本号
     * @param data
     * @return
     */
    private synchronized long getVersion(String data) {
        if (prefLoader == null && mContext == null) {
            return PreferencesLoader.DEFAULT_VALUE;
        }
        if (prefLoader == null) {
            prefLoader = new PreferencesLoader(mContext);
        }
        long version = prefLoader.getVersion(data);
        if (DEBUG) Log.i(TAG, String.format("getVersion %s : %d", data, version));

        return version;
    }

    private void notifyChange() {
        //todo notify launcher to reget icon
    }


    private Func1<String, List<FlymeIconBean>> queryStr2FlymeBeanF = new Func1<String, List<FlymeIconBean>>() {
        @Override
        public List<FlymeIconBean> call(String s) {
            try {
                String appKey = IconUtils.makeAppKey();
                Call<BaseEntity> callBean = getServerSingleton().fetchInfos(appKey, s);
                if (DEBUG) {
                    Log.i(TAG, "appkey: " + appKey);
                    Log.i(TAG, KEY_PACKAGE + ": " + s);
                }
                BaseEntity entity = callBean.execute().body();
                return entity.getData().values;
            } catch (Exception e) {
                e.printStackTrace();
                throw new FetchUrlFailedException(s);
            }
        }
    };


    /**
     * 对比版本号，需要更新时
     * 将fetchInfo传回的资源地址转为DownloadBean列表保存
     */
    private Func1<List<FlymeIconBean>, List<DownloadBean>> flymeBean2DownloadBeansF = new Func1<List<FlymeIconBean>, List<DownloadBean>>() {
        @Override
        public List<DownloadBean> call(List<FlymeIconBean> beans) {
            ArrayList<DownloadBean> result = new ArrayList<>();

            if (DEBUG) {
                Log.e(TAG, "get bean: " + TextUtils.join("," , beans));
            }
            for (FlymeIconBean b : beans) {
                long lastUpdate = getVersion(b.getPackageName());
                long serverUpdate = b.getUpdateAt();
                if (serverUpdate > lastUpdate) {
                    DownloadBean bitmapBean = new DownloadBean();
                    String lUrl = IconUtils.getUrl(b, DPI, IconUtils.TYPE_LAUNCHER);
                    String sUrl = IconUtils.getUrl(b, DPI, IconUtils.TYPE_STATUS);
                    try {
                        if (lUrl != null) {
                            bitmapBean.lIs = getServerSingleton().getIcon(lUrl).execute().body();
                            bitmapBean.isValid = true;
                        }
                        if (sUrl != null) {
                            bitmapBean.sIs = getServerSingleton().getIcon(sUrl).execute().body();
                            bitmapBean.isValid = true;
                        }
                        bitmapBean.updateAt = serverUpdate;
                        bitmapBean.pkgName = b.getPackageName();
                        result.add(bitmapBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new GetInputStreamFailedException(b.getIconL());
                    }
                }
            }
            return result;
        }
    };

    private Func1<List, Boolean> ifListValiedF = new Func1<List, Boolean>() {
        @Override
        public Boolean call(List list) {
            boolean result = (list != null && (!list.isEmpty()));
            if (!result) Log.e(TAG, "list invalid");
            return result;
        }
    };


    /**
     * 将网络数据保存到sdcard
     */
    private Action1<List<DownloadBean>> saveDownloadBitmapA = new Action1<List<DownloadBean>>() {
        @Override
        public void call(List<DownloadBean> downloads) {
            for (DownloadBean download : downloads) {
                IconUtils.saveBitmap(IconUtils.TYPE_LAUNCHER, download.pkgName, download.lIs);
                IconUtils.saveBitmap(IconUtils.TYPE_STATUS, download.pkgName, download.sIs);
            }
        }
    };

    /**
     * 结束之后的更新操作
     */
    Subscriber<List<DownloadBean>> onFinishNetWork =  new Subscriber<List<DownloadBean>>() {
        @Override
        public void onError(Throwable e) {
            if (e instanceof FetchUrlFailedException) {
                String s = e.getMessage();
                if (!s.contains(KEY_IN)) {
                    // TODO: 17-6-27 定期检查更新失败暂时不做下载失败处理
                    String pkg = IconUtils.unGson2Pkg(s);
                    startScheduler(JOB_RESTART_DOWNLOAD, pkg);
                    Log.e(TAG, "startScheduler: " + pkg);
                }
            } else
            if (e instanceof GetInputStreamFailedException) {
                startScheduler(JOB_RESTART_DOWNLOAD, e.getMessage());
                Log.e(TAG, "startScheduler: " + e.getMessage());
            }
        }
        @Override
        public void onNext(List<DownloadBean> downloadBeans) {
            updateVersion(downloadBeans);
            notifyChange();
            updateFlymeIconTheme();
        }

        @Override
        public void onCompleted() {}
    };

    /**
     * 结束之后的更新操作
     */
    Subscriber<List<DownloadBean>> onFinishNetWorkNotRetry =  new Subscriber<List<DownloadBean>>() {
        @Override
        public void onError(Throwable e) {
            //ignore
        }
        @Override
        public void onNext(List<DownloadBean> downloadBeans) {
            updateVersion(downloadBeans);
            notifyChange();
            updateFlymeIconTheme();
        }

        @Override
        public void onCompleted() {}
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
        } else {
            Log.e(TAG, "can't get scheduler when context = n");
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
//                builder.setPersisted(true); //todo 非系统应用无法成功申请boot-complete权限？
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
/**
 * Created by huangminzhi on 17-6-20.
 * 客户端主要逻辑接口
 */
interface IconEvent {
    void pullIcon(Context context, String data, boolean needRetry);
    void checkIcons(Context context, boolean needRetry);
    void setScheduleCheck(Context context);
}

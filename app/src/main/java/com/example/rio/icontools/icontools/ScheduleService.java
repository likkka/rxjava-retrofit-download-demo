package com.example.rio.icontools.icontools;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.example.rio.icontools.icontools.IconManager;

/**
 * Created by huangminzhi on 17-6-22.
 * 定时处理的业务，包括单独下载图标失败，会重新定时获取
 */

public class ScheduleService extends JobService {
    private static final String TAG = "ScheduleService";

    @Override
    public boolean onStartJob(JobParameters params) {
        int jobId = params.getJobId();
        switch (jobId) {
            case IconManager.JOB_INTERVAL_CHECK:
                Log.i(TAG, "start JOB_INTERVAL_CHECK");
                IconManager.getInstance().checkIcons(getApplicationContext(), false);
                break;
            case IconManager.JOB_RESTART_DOWNLOAD:
                String pkg = params.getExtras().getString(IconManager.KEY_PACKAGE);
                if(pkg == null) return true;
                Log.i(TAG, "start JOB_RESTART_DOWNLOAD: " + pkg);
                IconManager.getInstance().pullIcon(getApplicationContext(), pkg, false);
                break;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

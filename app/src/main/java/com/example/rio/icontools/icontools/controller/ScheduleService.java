package com.example.rio.icontools.icontools.controller;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by huangminzhi on 17-6-22.
 * 定时处理的业务，包括单独下载图标失败，会重新定时获取
 */

public class ScheduleService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        int jobId = params.getJobId();
        switch (jobId) {
            case IconEventController.JOB_INTERVAL_CHECK:
                IconEventController.getInstance().checkIcons(getApplicationContext());
                break;
            case IconEventController.JOB_RESTART_DOWNLOAD:
                String pkg = params.getExtras().getString(IconEventController.KEY_PACKAGE);
                if(pkg == null) return true;
                IconEventController.getInstance().pullIcon(getApplicationContext(), pkg);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

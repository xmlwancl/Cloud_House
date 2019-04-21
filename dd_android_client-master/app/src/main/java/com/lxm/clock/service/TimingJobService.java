package com.lxm.clock.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lxm.DdApplication;
import com.lxm.util.LogUtils;
import com.lxm.util.ServiceUtils;

@SuppressWarnings("NewApi")
public class TimingJobService extends JobService {

    private static final String TAG = "TimingJobService";

    public static final String ACTION_WAKE_SCREEN = "action_wake_screen";

    private static int mJobId = 0x1;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.logErrorInDebugMode(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logErrorInDebugMode(TAG, "onStartCommand");
        scheduleJob(this, getJobInfo(this));
        return START_STICKY;
    }

    public static void startJob(Context context) {
        scheduleJob(context, getJobInfo(context));
    }

    public static void scheduleJob(Context context, JobInfo jobInfo) {
        JobScheduler tm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tm = context.getSystemService(JobScheduler.class);
        } else {
            tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        if (tm != null) {
            tm.cancel(mJobId);
            tm.schedule(jobInfo);
        }
    }

    public static JobInfo getJobInfo(Context context) {
        JobInfo jobInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(mJobId++, new ComponentName(context, TimingJobService.class))
                    .setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true).build();
        } else {
            jobInfo = new JobInfo.Builder(mJobId++, new ComponentName(context, TimingJobService.class))
                    .setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true).build();
        }
        return jobInfo;
    }

    private void doJob(JobParameters params) {
        if (!ServiceUtils.isServiceRunning(DdApplication.app, ClockService.class.getName())) {
            startService(new Intent(this, ClockService.class));
        }

        sendBroadcast(new Intent(ACTION_WAKE_SCREEN));
        restartJob(params);
    }

    private void restartJob(JobParameters params) {
        startJob(DdApplication.app);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobFinished(params, true);
        } else {
            jobFinished(params, false);
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        doJob(params);
        LogUtils.logErrorInDebugMode(TAG, "onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.logErrorInDebugMode(TAG, "onStopJob");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logErrorInDebugMode(TAG, "onDestroy");
        startService(new Intent(this, TimingJobService.class));
    }
}

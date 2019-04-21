package com.lxm.clock.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.lxm.R;
import com.lxm.ScreenObserver;
import com.lxm.clock.helper.DdHelper;
import com.lxm.clock.ui.AgentActivity;
import com.lxm.clock.ui.DdAlarmActivity;
import com.lxm.common.service.DdNotificationListenerService;
import com.lxm.util.DateUtils;
import com.lxm.util.LogUtils;
import com.lxm.util.Utils;

import java.util.Date;
import java.util.List;

public class ClockService extends Service {

    private static final String TAG = "ClockService";
    private ScreenObserver screenObserver;
    private boolean ScreenState=false;
    @Override
    public void onCreate() {
        super.onCreate();
        registReceiver();
        LogUtils.logErrorInDebugMode(TAG, "onCreate");
        screenObserver=new ScreenObserver(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        setNotification();
        LogUtils.logErrorInDebugMode(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification();
        LogUtils.logErrorInDebugMode(TAG, "onStartCommand");
        return START_STICKY;
    }

    private void registReceiver() {
        IntentFilter timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeChangeReceiver, timeFilter);

        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(DdNotificationListenerService.ACTION_NOTIFICATION_POSTED);
        notificationFilter.addAction(DdNotificationListenerService.ACTION_NOTIFICATION_REMOVED);
        registerReceiver(notificationReceiver, notificationFilter);

        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(TimingJobService.ACTION_WAKE_SCREEN);
        registerReceiver(screenReceiver, screenFilter);
    }

    private void setNotification() {
        Intent intent = new Intent(this, DdAlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, "dd").setContentTitle("助手")
                .setContentText("助手").setContentIntent(pendingIntent).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher)).build();
        startForeground(1, notification);
    }

    public void startAlarm() {
        List<String> timeStr = DdHelper.getDdAlarmTime(this);
        String time = DateUtils.hm.format(new Date());
        for (String t : timeStr) {
            if (time.equals(t)) {
                startAgentActivity();
            }
        }
    }

    private void startAgentActivity() {
        if (checkCanStart()) {
//            try {
//                Intent intent = new Intent(this, AgentActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
        }
    }

    public static boolean checkCanStart() {
        if (Utils.isComponentDisenabled(AgentActivity.class)) {
            Utils.setComponentEnable(AgentActivity.class);
        }
        return !Utils.isComponentDisenabled(AgentActivity.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logErrorInDebugMode(TAG, "onDestroy");
        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(notificationReceiver);
        unregisterReceiver(screenReceiver);
        //停止监听screen状态
        screenObserver.stopScreenStateUpdate();

        startService(new Intent(this, ClockService.class));
    }

    private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startAlarm();
            ScreenState=true;
        }
    };

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkg = intent.getStringExtra("pkg");
            if (!TextUtils.isEmpty(pkg)) {
                LogUtils.logInfoInDebugMode(TAG, "notification pkg: " + pkg);
            }
        }
    };

    private BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtils.logInfoInDebugMode(TAG, "screen action: " + intent.getAction()+ScreenState);
            if(intent.getAction().equals("android.intent.action.SCREEN_ON")&&ScreenState){
                Log.i(TAG, "screenObserver");
                Intent intents = new Intent(context, AgentActivity.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intents);
                ScreenState=false;
//                screenObserver.requestScreenStateUpdate(new ScreenObserver.ScreenStateListener() {
//                    @Override
//                    public void onScreenOn() {
//                        ScreenState=true;
//                        Log.i(TAG, "onScreenOn");
//                    }
//
//                    @Override
//                    public void onScreenOff() {
//                        //  doSomethingOnScreenOff();
//                        ScreenState=false;
//                        Log.i(TAG, "onScreenOff");
//                    }
//
//                    @Override
//                    public void onUserPresent(){
//                        ScreenState=true;
//                        Log.i(TAG, "onUserPresent");
//                    }
//                });
            }
        }
    };
}

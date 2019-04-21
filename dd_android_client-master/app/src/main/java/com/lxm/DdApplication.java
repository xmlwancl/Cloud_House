package com.lxm;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.lxm.clock.service.ClockService;
import com.lxm.clock.service.TimingJobService;
import com.lxm.umeng.CustomUmengMessageHandler;
import com.lxm.util.LogUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

public class DdApplication extends Application {

    public static DdApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initUmengPush(this);
        bindClockService();
        startTimingService();
    }

    private void bindClockService() {
        bindService(new Intent(this, ClockService.class), lockConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection lockConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindClockService();
        }
    };

    private void startTimingService() {
        TimingJobService.startJob(this);
    }

    private void initUmengPush(Context context) {
        try {
            UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, "aa1e4789abf9ee6244ede9989c6cfbd9");
            PushAgent mPushAgent = PushAgent.getInstance(context);
            mPushAgent.register(new IUmengRegisterCallback() {
                @Override
                public void onSuccess(String s) {
                    LogUtils.logInfoInDebugMode("hsp", "device token " + s);
                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
            mPushAgent.setMessageHandler(new CustomUmengMessageHandler());
        } catch (Throwable e) {
            Log.e("hsp", "push", e);
        }
    }
}

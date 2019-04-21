package com.lxm.clock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.lxm.R;
import com.lxm.ScreenObserver;
import com.lxm.common.ui.NewAppCompatActivity;

public class AgentActivity extends NewAppCompatActivity {
    private Handler mHandler;
    private static final String TAG = "Agent";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        initWindowFeature();
        setContentView(R.layout.activity_agent);
        init();

    }

    private void initWindowFeature() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void init() {
        mHandler = new Handler();
    }

    private Runnable redirectRunnable = new Runnable() {
        @Override
        public void run() {
            startDd();
        }
    };

    private void startDd() {
        try {
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.alibaba.android.rimet",
//                    "com.alibaba.android.rimet.biz.SplashActivity"));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            Log.i(TAG, "Intent");
            Intent launchIntentForPackage = this.getPackageManager().getLaunchIntentForPackage("com.kdweibo.client");
            launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(launchIntentForPackage);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeCallbacks(redirectRunnable);
        mHandler.postDelayed(redirectRunnable, 3000L);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

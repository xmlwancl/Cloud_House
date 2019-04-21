package com.lxm.common.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;

import com.lxm.util.LogUtils;

import java.util.Set;

public class DdNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "DdNotificationListenerS";

    public static final String ACTION_NOTIFICATION_POSTED = "action_notification_posted";
    public static final String ACTION_NOTIFICATION_REMOVED = "action_notification_removed";

    @Override
    public void onListenerConnected() {
        LogUtils.logErrorInDebugMode(TAG, "onListenerConnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        sendBroadcast(new Intent(ACTION_NOTIFICATION_POSTED).putExtra("pkg", sbn.getPackageName()));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        sendBroadcast(new Intent(ACTION_NOTIFICATION_REMOVED).putExtra("pkg", sbn.getPackageName()));
    }

    @Override
    public void onListenerDisconnected() {
        rebindListenerService();
    }

    private void rebindListenerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(this, DdNotificationListenerService.class));
        } else {
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(this, DdNotificationListenerService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(this, DdNotificationListenerService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }

    public static void openNotificationListenerSetting(Context context) {
        try {
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

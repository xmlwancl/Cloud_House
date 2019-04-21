package com.lxm.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.lxm.DdApplication;

public class Utils {

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
                context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        return w_screen;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int h_screen = dm.heightPixels;
        return h_screen;
    }

    public static boolean isComponentDisenabled(Class cls) {
        final PackageManager pm = DdApplication.app.getPackageManager();
        return pm.getComponentEnabledSetting(
                new ComponentName(DdApplication.app, cls)) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public static void setComponentEnable(Class cls) {
        final PackageManager pm = DdApplication.app.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(DdApplication.app, cls),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void setComponentEnable(String pkg, String cls) {
        final PackageManager pm = DdApplication.app.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(pkg, cls),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}

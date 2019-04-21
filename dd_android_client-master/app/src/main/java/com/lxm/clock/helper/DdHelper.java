package com.lxm.clock.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lxm.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DdHelper {

    private static final String DD_PREFERENCE_FILE = "dd_preference_file";

    private static final String DD_ALARM_TIME = "dd_alarm_time";

    public static List<String> getDdAlarmTime(Context context) {
        List<String> timeList = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(DD_PREFERENCE_FILE, Context.MODE_PRIVATE);
        String storedTime = preferences.getString(DD_ALARM_TIME, null);
        if (!TextUtils.isEmpty(storedTime)) {
            String[] arr = storedTime.split("&");
            for (String t : arr) {
                if (!TextUtils.isEmpty(t)) {
                    timeList.add(t);
                }
            }
        }
        return timeList;
    }

    public static boolean addAlarmTimeToPreference(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(DD_PREFERENCE_FILE, Context.MODE_PRIVATE);
        String ddAlarmTime = preferences.getString(DD_ALARM_TIME, "");
        if (!TextUtils.isEmpty(ddAlarmTime)) {
            String[] timeArr = ddAlarmTime.split("&");
            for (String s : timeArr) {
                if (time.equals(s)) {
                    Utils.showToast(context, "不允许设置相同时间");
                    return false;
                }
            }
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DD_ALARM_TIME, ddAlarmTime + "&" + time);
        editor.apply();
        return true;
    }

    public static void deleteClock(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(DD_PREFERENCE_FILE, Context.MODE_PRIVATE);
        String storedTime = preferences.getString(DD_ALARM_TIME, null);
        if (TextUtils.isEmpty(storedTime)) {
            return;
        }
        String finalTime = storedTime.replaceAll("&*" + time, "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DD_ALARM_TIME, finalTime);
        editor.apply();
    }
}

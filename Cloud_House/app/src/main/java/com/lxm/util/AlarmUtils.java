package com.lxm.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmUtils {

    public static void setSystemAlarm(Context context, boolean add, int hourOfDay, int minute) {
        if (!add && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(context, "手机系统版本过低，无法取消闹钟", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ArrayList<Integer> alarmDays = new ArrayList<>();
            alarmDays.add(Calendar.MONDAY);
            alarmDays.add(Calendar.TUESDAY);
            alarmDays.add(Calendar.WEDNESDAY);
            alarmDays.add(Calendar.THURSDAY);
            alarmDays.add(Calendar.FRIDAY);

            Intent intent;
            if (add) {
                intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            } else {
                intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
            }
            intent.putExtra(AlarmClock.EXTRA_DAYS, alarmDays);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hourOfDay);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "该打开钉钉啦");
            intent.putExtra(AlarmClock.EXTRA_VIBRATE, false);
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (add) {
                Toast.makeText(context, "设置闹钟失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "取消闹钟失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

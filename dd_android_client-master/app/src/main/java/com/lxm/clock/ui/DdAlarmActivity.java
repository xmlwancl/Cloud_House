package com.lxm.clock.ui;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lxm.R;
import com.lxm.clock.adapter.AlarmAdapter;
import com.lxm.clock.helper.DdHelper;
import com.lxm.common.DdPreferenceManager;
import com.lxm.common.PrefConstants;
import com.lxm.common.service.DdNotificationListenerService;
import com.lxm.common.ui.NewAppCompatActivity;
import com.lxm.util.AlarmUtils;
import com.lxm.util.BitmapUtils;
import com.lxm.util.DateUtils;
import com.lxm.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 定时打开钉钉
 */
public class DdAlarmActivity extends NewAppCompatActivity implements AlarmAdapter.ItemClickListener {

    @BindView(R.id.iv_time1)
    ImageView ivHour1;
    @BindView(R.id.iv_time2)
    ImageView ivHour2;
    @BindView(R.id.iv_time3)
    ImageView ivMinute1;
    @BindView(R.id.iv_time4)
    ImageView ivMinute2;
    @BindView(R.id.iv_time5)
    ImageView ivSecond1;
    @BindView(R.id.iv_time6)
    ImageView ivSecond2;
    @BindView(R.id.rv_alarm_record)
    RecyclerView alarmRecordRecycler;
    @BindView(R.id.tv_alarm_no_clock)
    TextView noClockTv;

    private Unbinder unbinder;
    private AlarmAdapter adapter;
    private TimePickerDialog timePickerDialog;
    private List<String> alarmDatas;
    private Handler mHandler;
    private SparseIntArray bitmapResIds;
    private ArrayMap<Integer, Bitmap> timePicRes;
    private AlertDialog deleteDialog, permissionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dd);
        unbinder = ButterKnife.bind(this);
        init();
        initData();
        initView();
        checkNotificationListenerPermission();
        requestPermission();
    }

    private void init() {
        mHandler = new Handler();
    }

    private void initData() {
        alarmDatas = new ArrayList<>();
        List<String> ddAlarmTimes = DdHelper.getDdAlarmTime(this);
        alarmDatas.addAll(ddAlarmTimes);

        loadTimeBitmap();
    }

    private void loadTimeBitmap() {
        bitmapResIds = new SparseIntArray();
        bitmapResIds.put(0, R.drawable.number_0);
        bitmapResIds.put(1, R.drawable.number_1);
        bitmapResIds.put(2, R.drawable.number_2);
        bitmapResIds.put(3, R.drawable.number_3);
        bitmapResIds.put(4, R.drawable.number_4);
        bitmapResIds.put(5, R.drawable.number_5);
        bitmapResIds.put(6, R.drawable.number_6);
        bitmapResIds.put(7, R.drawable.number_7);
        bitmapResIds.put(8, R.drawable.number_8);
        bitmapResIds.put(9, R.drawable.number_9);

        timePicRes = new ArrayMap<>();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = BitmapUtils.getInSampleSize(this, R.drawable.number_0,
                Utils.dip2px(this, 30), Utils.dip2px(this, 40));

        Resources contextResouce = getResources();
        for (int i = 0; i < bitmapResIds.size(); i++) {
            Bitmap timeBitmap = BitmapFactory.decodeResource(contextResouce, bitmapResIds.get(i), options);
            timePicRes.put(i, timeBitmap);
        }
    }

    private void initView() {
        adapter = new AlarmAdapter(this, alarmDatas);
        adapter.setItemClickListener(this);
        alarmRecordRecycler.setAdapter(adapter);
        alarmRecordRecycler.setLayoutManager(new LinearLayoutManager(this));

        mHandler.postDelayed(timeRunnable, 1000);

        if (alarmDatas.isEmpty()) {
            noClockTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dd_alarm, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_alarm).setIcon(R.drawable.account_add_icon);
        menu.findItem(R.id.menu_setting).setIcon(R.drawable.three_dot_icon_black);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_alarm) {
            showTimePickDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_setting) {
         //   startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimePickDialog() {
        if (timePickerDialog == null) {
            timePickerDialog = new TimePickerDialog(this, timeSetListener, 9, 0, true);
        }
        timePickerDialog.show();
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            addAlarmTime(hourOfDay, minute);
        }
    };

    private void addAlarmTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);
        String time = DateUtils.hm.format(calendar.getTime());

        boolean addTime = DdHelper.addAlarmTimeToPreference(this, time);
        if (addTime) {
            noClockTv.setVisibility(View.GONE);
            alarmDatas.add(time);
            adapter.notifyDataSetChanged();
        }
//        boolean addSystemAlarm = DdPreferenceManager.getIntance().getPrefBoolean(PrefConstants.PREF_ADD_SYSTEM_ALARM, true);
//        if (addSystemAlarm) {
//            AlarmUtils.setSystemAlarm(this, true, hourOfDay, minute);
//        }
    }

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            updateCurrentTime();
            mHandler.postDelayed(timeRunnable, 1000);
        }
    };

    private void updateCurrentTime() {
        String timeStr = DateUtils.hms.format(new Date());

        int hour1 = Integer.parseInt(timeStr.charAt(0) + "");
        int hour2 = Integer.parseInt(timeStr.charAt(1) + "");
        int minute1 = Integer.parseInt(timeStr.charAt(3) + "");
        int minute2 = Integer.parseInt(timeStr.charAt(4) + "");
        int second1 = Integer.parseInt(timeStr.charAt(6) + "");
        int second2 = Integer.parseInt(timeStr.charAt(7) + "");

        ivHour1.setImageBitmap(timePicRes.get(hour1));
        ivHour2.setImageBitmap(timePicRes.get(hour2));
        ivMinute1.setImageBitmap(timePicRes.get(minute1));
        ivMinute2.setImageBitmap(timePicRes.get(minute2));
        ivSecond1.setImageBitmap(timePicRes.get(second1));
        ivSecond2.setImageBitmap(timePicRes.get(second2));
    }

    @Override
    public void onItemClick(int position) {
        if (position < alarmDatas.size()) {
            showDeleteDialog(position);
        }
    }

    private void showDeleteDialog(final int position) {
        String time = alarmDatas.get(position);
        SpannableString content = new SpannableString("确认删除" + time + "这个闹钟？");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.main_red));
        content.setSpan(colorSpan, 4, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
        content.setSpan(sizeSpan, 4, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        deleteDialog = new AlertDialog.Builder(this).setTitle("温馨提示")
                .setMessage(content).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteDialog.isShowing()) {
                            deleteDialog.dismiss();
                        }
                        realDeleteClock(position);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteDialog.isShowing()) {
                            deleteDialog.dismiss();
                        }
                    }
                }).create();
        deleteDialog.show();
    }

    private void realDeleteClock(int position) {
        String time = alarmDatas.get(position);
        DdHelper.deleteClock(this, time);
        alarmDatas.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, alarmDatas.size() - position);
        if (alarmDatas.isEmpty()) {
            noClockTv.setVisibility(View.VISIBLE);
        }

//        boolean addSystemAlarm = DdPreferenceManager.getIntance().getPrefBoolean(PrefConstants.PREF_ADD_SYSTEM_ALARM, true);
//        if (addSystemAlarm) {
//            String[] timeStrArr = time.split(":");
//            int hour = Integer.parseInt(timeStrArr[0]);
//            int minute = Integer.parseInt(timeStrArr[1]);
//            AlarmUtils.setSystemAlarm(this, false, hour, minute);
//        }
    }

    private void checkNotificationListenerPermission() {
        if (DdNotificationListenerService.isNotificationListenerEnabled(this)) {
            return;
        }
        permissionDialog = new AlertDialog.Builder(this)
                .setTitle("温馨提示").setMessage("需要打开通知权限提高保活效果")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionDialog.dismiss();
                        DdNotificationListenerService.openNotificationListenerSetting(DdAlarmActivity.this);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionDialog.dismiss();
                    }
                }).create();
        permissionDialog.show();
    }

    private void requestPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SET_ALARM}, 100);
        }
    }

    private void recycleTimeBitmaps() {
        for (int i = 0; i < timePicRes.size(); i++) {
            Bitmap timeBitmap = timePicRes.get(i);
            if (timeBitmap != null && !timeBitmap.isRecycled()) {
                timeBitmap.recycle();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this).setTitle("警告")
                        .setMessage("没有授予权限，应用即将退出！")
                        .setCancelable(false).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timePickerDialog != null && timePickerDialog.isShowing()) {
            timePickerDialog.dismiss();
            timePickerDialog = null;
        }
        mHandler.removeCallbacks(timeRunnable);
        unbinder.unbind();
        recycleTimeBitmaps();
    }
}

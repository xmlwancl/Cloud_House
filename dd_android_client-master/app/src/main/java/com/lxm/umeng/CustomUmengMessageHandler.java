package com.lxm.umeng;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.lxm.clock.service.ClockService;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

public class CustomUmengMessageHandler extends UmengMessageHandler {

    @Override
    public void dealWithCustomMessage(final Context context, final UMessage message) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                context.startService(new Intent(context, ClockService.class));
            }
        });
    }
}

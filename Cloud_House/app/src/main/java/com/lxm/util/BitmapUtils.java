package com.lxm.util;

import android.content.Context;
import android.graphics.BitmapFactory;

public class BitmapUtils {

    public static int getInSampleSize(Context context, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        int inSampleSize = 1;
        if (imageWidth > reqWidth || imageHeight > reqHeight) {
            int halfWidth = imageWidth / 2;
            int halfHeight = imageHeight / 2;
            while (halfWidth / inSampleSize > reqWidth && halfHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}

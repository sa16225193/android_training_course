package com.example.administrator.android_training_course.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2018/1/30.
 */

public class BitmapUtil {

    /**
     * 压缩图片得到Bitmap
     * @param res Context.getResource()
     * @param resId    图片的资源Id
     * @param reqWidth  压缩后的图片宽
     * @param reqHeight 压缩后的图片高
     * @return
     */
    public static Bitmap compressBitmap(Resources res, int resId, int reqWidth, int reqHeight) {

        //得到图片的原始尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //计算图片的压缩比例
        int inSampleSize = 1;
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        //压缩图片
        return BitmapFactory.decodeResource(res, resId, options);
    }
}

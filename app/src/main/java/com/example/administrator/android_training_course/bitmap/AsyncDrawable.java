package com.example.administrator.android_training_course.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * 通过专用的Drawable来保存AsyncTask的引用，
 * 来处理ListView的ViewHolder复用引起的并发问题
 * Created by Administrator on 2018/1/30.
 */

public class AsyncDrawable extends BitmapDrawable{

    private final WeakReference bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res,Bitmap bitmap,
                         BitmapWorkerTask task) {
        super(res, bitmap);
        bitmapWorkerTaskReference = new WeakReference(task);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return (BitmapWorkerTask) bitmapWorkerTaskReference.get();
    }
}

package com.example.administrator.android_training_course.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.administrator.android_training_course.utils.BitmapUtil;

import java.lang.ref.WeakReference;

/**
 * 异步加载得到Bitmap
 * Created by Administrator on 2018/1/30.
 */

public class BitmapWorkerTask extends AsyncTask<Integer,Void,Bitmap> {

    private final WeakReference imageViewReference;
    private int data = 0;
    private Context mContext;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        imageViewReference = new WeakReference(imageView);
        mContext = context;
    }

    public int getData() {
        return data;
    }

    @Override
    protected Bitmap doInBackground(Integer... integers) {
        data = integers[0];
        return BitmapUtil.compressBitmap(mContext.getResources(), data, 100, 100);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = (ImageView) imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}

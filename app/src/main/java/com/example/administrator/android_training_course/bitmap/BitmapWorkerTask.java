package com.example.administrator.android_training_course.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.administrator.android_training_course.R;
import com.example.administrator.android_training_course.utils.BitmapUtil;

import java.lang.ref.WeakReference;

/**
 * 异步加载得到Bitmap
 * Created by Administrator on 2018/1/30.
 */

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    private final WeakReference imageViewReference;
    private int data = 0;
    private Context mContext;
    private Bitmap mPlaceHolderBitmap;
    private static LruCache<String, Bitmap> mMemoryCache;

    static {
        //得到应用的最大可用内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;//使用1/8作为Bitmap缓存

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }


    public BitmapWorkerTask(Context context, ImageView imageView) {
        imageViewReference = new WeakReference(imageView);
        mContext = context;
        mPlaceHolderBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    public int getData() {
        return data;
    }

    @Override
    protected Bitmap doInBackground(Integer... integers) {
        data = integers[0];
        Bitmap bitmap = getBitmapFromMemCache(String.valueOf(data));
        if (bitmap == null) {
            bitmap = BitmapUtil.compressBitmap(
                    mContext.getResources(), data, 100, 100);
            addBitmapToMemoryCache(String.valueOf(data), bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            final ImageView imageView = (ImageView) imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }else {
                    imageView.setImageBitmap(mPlaceHolderBitmap);
                }
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}

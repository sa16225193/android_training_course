package com.example.administrator.android_training_course.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.administrator.android_training_course.R;
import com.example.administrator.android_training_course.utils.BitmapUtil;

public class BitmapActivity extends AppCompatActivity {

    private ImageView iv_bitmap;
    private Bitmap mPlaceHolderBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        iv_bitmap = findViewById(R.id.iv_bitmap);
        iv_bitmap.setImageBitmap(BitmapUtil.compressBitmap(getResources(), R.mipmap.aaa, 200, 150));
        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(this, imageView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(
                    getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(R.mipmap.aaa);
        }
    }

    public boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.getData();
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
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

package com.example.administrator.android_training_course;


import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by liuyong on 2018/1/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Android7.0调用相机时出现新的错误
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }
}

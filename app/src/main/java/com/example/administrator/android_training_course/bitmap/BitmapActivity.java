package com.example.administrator.android_training_course.bitmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.administrator.android_training_course.R;
import com.example.administrator.android_training_course.utils.BitmapUtil;

public class BitmapActivity extends AppCompatActivity {

    private ImageView iv_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        iv_bitmap = findViewById(R.id.iv_bitmap);
        iv_bitmap.setImageBitmap(BitmapUtil.compressBitmap(getResources(), R.mipmap.aaa, 200, 150));
    }

}

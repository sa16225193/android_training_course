package com.example.administrator.android_training_course;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.android_training_course.R;
import com.example.administrator.android_training_course.actionbar.DisplayMessageActivity;
import com.example.administrator.android_training_course.fileshare.FileSelectActivity;
import com.example.administrator.android_training_course.fragment.FragmentTestActivity;
import com.example.administrator.android_training_course.media.AudioActivity;
import com.example.administrator.android_training_course.nfc.NFCActivity;
import com.example.administrator.android_training_course.photo.CameraActivity;
import com.example.administrator.android_training_course.photo.PhotoActivity;
import com.example.administrator.android_training_course.photo.VideoActivity;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.administrator.android_training_course.MESSAGE";
    public final static String URI_TO_IMAGE = "http://img.taopic.com/uploads/allimg/140320/235006-140320195A921.jpg";


    static void Log(String msg) {
        Log.d("TAG", "MainActivity: " + msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void sendMessage(View v) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.edit_message);
        intent.putExtra(EXTRA_MESSAGE, editText.getText().toString());
        startActivity(intent);
    }

    public void startFragment(View v) {
        Intent intent = new Intent(this, FragmentTestActivity.class);
        startActivity(intent);
    }

    public void share(View v){
//        Intent shareTextIntent = new Intent();
//        shareTextIntent.setAction(Intent.ACTION_SEND);
//        shareTextIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send");
//        shareTextIntent.setType("text/plain");
//        startActivity(Intent.createChooser(shareTextIntent, getResources().getText(R.string.button_share)));

        Intent shareBinaryIntent = new Intent();
        shareBinaryIntent.setAction(Intent.ACTION_SEND);
        shareBinaryIntent.putExtra(Intent.EXTRA_STREAM,URI_TO_IMAGE);
        shareBinaryIntent.setType("image/*");
        startActivity(Intent.createChooser(shareBinaryIntent, getResources().getText(R.string.button_share)));

    }

    public void shareFile(View v){
        Intent intent = new Intent(this, FileSelectActivity.class);
        startActivity(intent);
    }

    public void shareFileByNFC(View v) {
        Intent intent = new Intent(this, NFCActivity.class);
        startActivity(intent);
    }

    public void audioController(View v) {
        Intent intent = new Intent(this, AudioActivity.class);
        startActivity(intent);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    public void takeVideo(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    public void cameraTest(View v) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}

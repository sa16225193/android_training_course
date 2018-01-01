package com.example.administrator.android_training_course.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.android_training_course.R;

import java.net.URI;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.administrator.android_training_course.MESSAGE";
    public final static String URI_TO_IMAGE = "http://img.taopic.com/uploads/allimg/140320/235006-140320195A921.jpg";
    private int i;

    static void Log(String msg) {
        Log.d("TAG", "MainActivity: " + msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState!=null){
            Log("onCreate" + savedInstanceState.getInt("i"));
        }else {
            Log("onCreate");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log("onResume");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log("onRestoreInstanceState : " + savedInstanceState.getInt("i"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log("onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("i", i);
        Log("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log("onDestroy");
    }

    public void sendMessage(View v) {
        i++;
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

}

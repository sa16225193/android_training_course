package com.example.administrator.android_training_course.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.android_training_course.R;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.administrator.android_training_course.MESSAGE";
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

}

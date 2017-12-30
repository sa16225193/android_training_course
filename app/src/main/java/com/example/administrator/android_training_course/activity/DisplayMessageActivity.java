package com.example.administrator.android_training_course.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.android_training_course.R;

/**
 * Created by Administrator on 2017/12/23.
 */

class DisplayMessageActivity extends AppCompatActivity{

    static void Log(String msg) {
        Log.d("TAG", "DisplayMessageActivity: " + msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        //把 app icon 设置成可用的向上按钮：
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
        RelativeLayout layout = findViewById(R.id.content);
        layout.addView(textView);
        Log("onCreate");
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
        Log("onRestoreInstanceState");
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log("onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log("onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //为ActionBar扩展菜单项
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearch() {

    }

    private void openSettings() {

    }

}

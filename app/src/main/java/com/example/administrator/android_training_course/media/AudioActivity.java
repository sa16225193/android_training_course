package com.example.administrator.android_training_course.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.administrator.android_training_course.R;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = AudioActivity.class.getSimpleName();
    private AudioManager mAudioManager;//注册监听与取消监听媒体按钮事件
    private MyAudioFocusChangeListener mListener;
    private ComponentName mAudioController;
    private IntentFilter mIntentFilter;
    private NoisyAudioStreamReceiver mReceiver;
    private boolean mReceiverTag;
    private Button btn_main_play;
    private Button btn_main_pause;
    private Button btn_main_stop;
    private Button btn_main_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
        init();
    }

    private void initView() {
        btn_main_play = findViewById(R.id.btn_main_play);
        btn_main_pause = findViewById(R.id.btn_main_pause);
        btn_main_stop = findViewById(R.id.btn_main_stop);
        btn_main_exit = findViewById(R.id.btn_main_exit);
        btn_main_play.setOnClickListener(this);
        btn_main_pause.setOnClickListener(this);
        btn_main_stop.setOnClickListener(this);
        btn_main_exit.setOnClickListener(this);
    }

    private void init() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mListener = new MyAudioFocusChangeListener();
        mAudioController = new ComponentName(getPackageName(), AudioControlReceiver.class.getName());
        // 请求获取音频焦点
        int result = mAudioManager.requestAudioFocus(mListener,
                // 使用音频流
                AudioManager.STREAM_MUSIC,
                // 请求永久焦点
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {//请求成功
            mAudioManager.registerMediaButtonEventReceiver(mAudioController);//注册媒体按钮事件的广播接收器
            // Start playback.
        }
        //监听音频输出设备的状态，比如插入耳机
        mReceiver = new NoisyAudioStreamReceiver();
        mIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        checkHardware();
    }

    @Override
    public void onClick(View v) {

        if (btn_main_play == v) {//播放
            play();
        } else if (btn_main_stop == v) {//停止
            stop();
        } else if (btn_main_pause == v) {//暂停
            pause();
        } else if (btn_main_exit == v) {//退出
            exit();
        }
    }

    /**
     * 检测正在使用的硬件设备
     * 扬声器、耳机、蓝牙。。。
     */
    private void checkHardware() {
        if (mAudioManager.isBluetoothA2dpOn()) {
            // Adjust output for Bluetooth.
            Log.e(TAG, "使用蓝牙");
        } else if (mAudioManager.isSpeakerphoneOn()) {
            // Adjust output for Speakerphone.
            Log.e(TAG, "扬声器");
        } else if (mAudioManager.isWiredHeadsetOn()) {
            // Adjust output for headsets
            Log.e(TAG, "耳机");
        } else {
            // If audio plays and noone can hear it, is it still playing?
            Log.e(TAG, "其他");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private void exit() {
        Log.e(TAG, "exit");
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent); //停止服务
        finish();//退出Activity
    }

    private void pause() {
        Log.e(TAG, "pause");
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action", "pause");
        startService(intent);
    }

    private void stop() {
        Log.e(TAG, "stop");
        Intent intent = new Intent(this, MusicService.class);
        unregisterReceiver();
        intent.putExtra("action", "stop");
        startService(intent);
    }

    private void play() {
        Log.e(TAG, "play");
        Intent intent = new Intent(this, MusicService.class);
        if (!mReceiverTag) {//在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            mReceiverTag = true;//标识值 赋值为 true 表示广播已被注册
            //注册输出设备状态改变的广播接收器
            registerReceiver(mReceiver, mIntentFilter);
        }
        intent.putExtra("action", "play");
        startService(intent);
    }

    /**
     * 注销输出设备状态改变的广播接收器
     */
    private void unregisterReceiver() {
        Log.e(TAG, "unregisterReceiver");
        if (mReceiverTag) { //判断广播是否注册
            unregisterReceiver(mReceiver);//注销广播
            mReceiverTag = false;//Tag值 赋值为false 表示该广播已被注销
        }
    }

    /**
     * 音频焦点改变的监听器
     */
    private class MyAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {//失去Duck类型的短暂音频焦点，可降低当前音频音量
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
                Log.e(TAG, "失去Duck类型的短暂音频焦点，可降低当前音频音量");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {//获得音频焦点
                // Raise it back to normal
                play();
                Log.e(TAG,"获得音频焦点");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {//失去永久音频焦点
                mAudioManager.unregisterMediaButtonEventReceiver(mAudioController);//注销媒体按钮事件的广播接收器
                mAudioManager.abandonAudioFocus(mListener);//释放音频焦点的监听器
                // Stop playback
                stop();
                Log.e(TAG, "释放音频焦点的监听器");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {//失去短暂音频焦点
                // Pause playback
                pause();
                Log.e(TAG, "失去短暂音频焦点");
            }
        }
    }

}
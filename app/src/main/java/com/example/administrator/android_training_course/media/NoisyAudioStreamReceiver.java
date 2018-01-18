package com.example.administrator.android_training_course.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by Administrator on 2018/1/19.
 * 音频输出设备改变的广播监听器
 * 比如：插入耳机
 */

public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        //断开音频输出设备，拔出耳机或断开蓝牙
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
            serviceIntent.putExtra("action", "pause");
        }else {
            //插入耳机继续播放
            serviceIntent.putExtra("action", "play");
        }
        context.startService(serviceIntent);
    }
}

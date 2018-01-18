package com.example.administrator.android_training_course.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Administrator on 2018/1/18.
 * 接收用户按下控制按钮的广播
 */

public class AudioControlReceiver extends BroadcastReceiver {

    private static final String TAG = AudioControlReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {//按下媒体控制按钮
            Intent serviceIntent = new Intent(context, MusicService.class);
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {//按下play键
                // Handle key press.
                serviceIntent.putExtra("action", "play");
                Log.e(TAG, "按下play键");
            } else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {//按下暂停键
                serviceIntent.putExtra("action", "pause");
                Log.e(TAG, "按下暂停键");
            } else if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {//按下“下一首”
                serviceIntent.putExtra("action", "stop");
                Log.e(TAG, "按下“下一首”");
            } else if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()) {//音量--
                Log.e(TAG, "音量--");

            } else if (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()) {//音量++
                Log.e(TAG, "音量++");

            }
            context.startService(serviceIntent);
        }
    }
}

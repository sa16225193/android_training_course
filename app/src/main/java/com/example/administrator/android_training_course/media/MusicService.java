package com.example.administrator.android_training_course.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.administrator.android_training_course.R;

/**
 * Created by Administrator on 2018/1/19.
 */
public class MusicService extends Service {

    private MediaPlayer player;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //每次启动Service必须经过此方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        if("play".equals(action)){
            playMusic();
        }else if("stop".equals(action)){
            stopMusic();
        }else if("pause".equals(action)){
            pauseMusic();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {//停止服务前调用
        super.onDestroy();
        stopMusic();
    }
    private void exitMusic() { // 退出音乐
        stopMusic();
    }
    private void pauseMusic() {// 暂停音乐（续播）
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }
    private void stopMusic() {// 停止音乐（重播）
        if (player != null) {
            player.stop();
            player.reset();
            player.release();// 释放音乐资源
            player = null;
        }
    }
    private void playMusic() { // 播放音乐
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.a);
        }
        player.start();
    }
}

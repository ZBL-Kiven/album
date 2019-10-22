/*
package com.zj.album.ui.preview.helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

public class VoicePlayManager {

    private static final String START = "start";
    private static final String PAUSE = "pause";
    private static final String RESUME = "resume";
    private static final String STOP = "stop";
    private static final String COMPLETE = "complete";


    // TODO: 2019-10-22  
    */
/**
     * 用于管理音频焦点和播放通道
     *//*

    private AudioManager audioManager;
    */
/**
     * 用于控制距离感应器
     *//*

    private SensorManager sensorManager; //= Utils.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


    private Sensor proximitySensor;// = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


    private boolean proximity = false;

    private boolean isEarpieceMode = false;

    // TODO: 2019-10-22


    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };

    private SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float value = event.values[0];
            proximity = false;
            if (value == proximitySensor.getMaximumRange()) {
                if (!proximity) {
                    return;
                }
                stopEarpieceModePlay();
                proximity = false;
            } else {
                if (proximity) {
                    return;
                }
                startEarpieceModePlay()
                proximity = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    */
/**
     * 请求音频焦点，会暂停其他地方的播放
     *//*

    private void requestAudioFocus() {
        audioManager.requestAudioFocus(onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    */
/**
     * 释放音频焦点，会恢复其他地方的播放
     *//*

    private void abandonAudioFocus() {
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }

    */
/**
     * 设置耳筒模式
     *//*

    private void setEarpieceMode(boolean isEarpieceMode) {
//        audioManager.mode = if (isEarpieceMode) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
        audioManager.setMode(isEarpieceMode ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(!isEarpieceMode);
        int streamType = isEarpieceMode ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
        audioManager.setStreamVolume(streamType, audioManager.getStreamVolume(streamType), AudioManager.FX_KEY_CLICK);
    }

    */
/**
     * 停止耳桶模式
     *//*

    private void stopEarpieceModePlay() {
        if (!isEarpieceMode) {
            setEarpieceMode(false);
        }
    }

    */
/**
     * 開始耳桶模式播放
     *//*

    private void startEarpieceModePlay() {
        play(currentPlayFile, currentPlayMsgKey, getCurrentListener(), true);
        setEarpieceMode(true);
    }

    private void acquireScreenOnLock() {
        voicePlayListener?.acquireScreenOnLock()
//        val window = ActivityManager.getCurrentActivity()?.window
//        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private void releaseScreenOnLock() {
        voicePlayListener?.releaseScreenOnLock()
//        val window = ActivityManager.getCurrentActivity()?.window
//        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


}
*/

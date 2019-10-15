package com.zj.album.graphy.activity

import android.annotation.TargetApi
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.text.TextUtils
import com.zj.album.utils.SPStroage
import com.zj.album.utils.Utils
import java.io.File
import java.util.*

/**
 *
 * author cityfruit zys
 *
 *
 * 語音播放管理
 */
object VoicePlayManager {

    val START = "start"
    val PAUSE = "pause"
    val RESUME = "resume"
    val STOP = "stop"
    val COMPLETE = "complete"

    /**
     * 用于管理音频焦点和播放通道
     */
    private val audioManager = Utils.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {}

    /**
     * 请求音频焦点，会暂停其他地方的播放
     */
    fun requestAudioFocus() {
        audioManager.requestAudioFocus(onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
    }

    /**
     * 释放音频焦点，会恢复其他地方的播放
     */
    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(onAudioFocusChangeListener)
    }

    /**
     * 设置耳筒模式
     */
    private fun setEarpieceMode(isEarpieceMode: Boolean) {
//        audioManager.mode = if (isEarpieceMode) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
        audioManager.mode = if (isEarpieceMode) AudioManager.MODE_IN_CALL else AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = !isEarpieceMode
        val streamType = if (isEarpieceMode) AudioManager.STREAM_VOICE_CALL else AudioManager.STREAM_MUSIC
        audioManager.setStreamVolume(streamType, audioManager.getStreamVolume(streamType), AudioManager.FX_KEY_CLICK)
    }

    /**
     * 用于控制距离感应器
     */
    private val sensorManager = Utils.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private var proximity = false

    private val proximitySensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val value = event.values[0]
            proximity = if (value == proximitySensor.maximumRange) {
                if (!proximity) return
                stopEarpieceModePlay()
                false
            } else {
                if (proximity) return
                startEarpieceModePlay()
                true
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    /**
     * 停止耳桶模式
     */
    private fun stopEarpieceModePlay() {
        if (!isEarpieceMode) {
            setEarpieceMode(false)
        }
    }

    /**
     * 開始耳桶模式播放
     */
    private fun startEarpieceModePlay() {
        play(currentPlayFile, currentPlayMsgKey, getCurrentListener(), true)
        setEarpieceMode(true)
    }


    private fun acquireScreenOnLock() {
        voicePlayListener?.acquireScreenOnLock()
//        val window = ActivityManager.getCurrentActivity()?.window
//        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun releaseScreenOnLock() {
        voicePlayListener?.releaseScreenOnLock()
//        val window = ActivityManager.getCurrentActivity()?.window
//        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    /**
     * 播放逻辑
     */
    private lateinit var mediaPlayer: MediaPlayer
    private var voiceMsgPlayEventListener: VoiceMsgPlayEventListener? = null
    var voicePlayListener: VoicePlayListener? = null
    private var isVoicePlaying = false
    private var progressTimer: Timer? = null
    private var isEarpieceMode = false
    var currentPlayMsgKey: String? = null
    private var currentPlayFile: File? = null
    private var isPlayNextVoice = false

    private fun playLog(filename: String, msgKey: String, mode: String) {
    }

    fun play(file: File?, msgKey: String?, listenerMsg: VoiceMsgPlayEventListener?, isReplay: Boolean) {
        play(file, msgKey, listenerMsg, isReplay, 0, false)
    }

    /**
     * @param file 语音文件
     * @param msgKey 语音消息的 key
     * @param listener 语音播放时间的回调
     * @param isReplay 是否是重播
     * @param progress 播放进度
     * @param processByUser 是否是用户操作
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun play(file: File?, msgKey: String?, listener: VoiceMsgPlayEventListener?, isReplay: Boolean, progress: Int, processByUser: Boolean) {
        if (file == null || msgKey == null || listener == null) {
            return
        }
        when {
            isReplay -> {
                playLog(file.name, msgKey, "replay")
                stop()
            }

            isVoicePlaying && TextUtils.equals(currentPlayMsgKey, msgKey) -> {
                playLog(file.name, msgKey, "pause")
                if (processByUser) startOrResume(false, 0) else pause()
                return
            }
            !isVoicePlaying && TextUtils.equals(currentPlayMsgKey, msgKey) -> {
                playLog(file.name, msgKey, "resume")
                startOrResume(false, 0)
                return
            }

            isVoicePlaying || (!TextUtils.isEmpty(currentPlayMsgKey) && !TextUtils.equals(currentPlayMsgKey, msgKey)) -> {
                playLog(file.name, msgKey, "stop and start")
                stop()
            }

            else -> playLog(file.name, msgKey, "start")
        }

        currentPlayMsgKey = msgKey
        currentPlayFile = file
        isEarpieceMode = SPStroage.earpieceMode

        voiceMsgPlayEventListener = listener

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(if (isEarpieceMode || isReplay || (isPlayNextVoice && !isEarpieceMode)) AudioManager.STREAM_VOICE_CALL else AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(Utils.context, Uri.fromFile(file))
        mediaPlayer.setOnPreparedListener { startOrResume(true, progress) }
        mediaPlayer.setWakeMode(Utils.context, PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)
        mediaPlayer.setOnCompletionListener { onCompletion(false) }
        mediaPlayer.prepareAsync()
        if (!isReplay && !isPlayNextVoice) {
            setEarpieceMode(isEarpieceMode)
        }
    }

    private fun getCurrentListener() = voiceMsgPlayEventListener

    private fun startOrResume(isStart: Boolean, progress: Int) {
        requestAudioFocus()
        acquireScreenOnLock()
        mediaPlayer.start()
        if (progress != 0) {
            mediaPlayer.seekTo(getDuration() / 100 * progress)
        }
        isVoicePlaying = true
        voicePlayListener?.onStart()

        getCurrentListener()?.onEvent(if (isStart) START else RESUME, currentPlayMsgKey)
        startProgressTimer()
        sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun pause() {
        mediaPlayer.pause()

        isVoicePlaying = false
        getCurrentListener()?.onEvent(PAUSE, currentPlayMsgKey)
        voicePlayListener?.onStop(false, null)
        stopProgressTimer()
        sensorManager.unregisterListener(proximitySensorListener)
    }

    private fun stop() {
        mediaPlayer.stop()
        onCompletion(true)
    }

    /**
     * 播放完成后的相关处理
     */
    private fun onCompletion(isStop: Boolean) {
        isVoicePlaying = false

        val listener = getCurrentListener()
        listener?.onEvent(if (isStop) STOP else COMPLETE, currentPlayMsgKey)
        resetProgress()

        mediaPlayer.release()

        val copyKey = currentPlayMsgKey
        currentPlayMsgKey = null

        isPlayNextVoice = voicePlayListener?.onStop(!isStop, copyKey) ?: false

        if (!isPlayNextVoice) {
            sensorManager.unregisterListener(proximitySensorListener)
            setEarpieceMode(false)
            abandonAudioFocus()
            releaseScreenOnLock()
        }
    }

    /**
     * 设置播放进度
     */
    fun setProgress(progress: Int, msgKey: String?) {
        if (TextUtils.equals(msgKey, currentPlayMsgKey)) {
            mediaPlayer.seekTo(getDuration() / 100 * progress)
        }
    }

    /**
     * 重置进度
     */
    private fun resetProgress() {
        stopProgressTimer()
        getCurrentListener()?.onProgress(0, currentPlayMsgKey)
    }

    /**
     * 停止线程发送当前进度
     */
    private fun stopProgressTimer() {
        progressTimer?.cancel()
        progressTimer = null
    }

    /**
     * 启动线程发送当前进度
     */
    private fun startProgressTimer() {
        if (progressTimer == null) {
            progressTimer = Timer()
        }

        progressTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (isVoicePlaying) {
                    getCurrentListener()?.onProgress((mediaPlayer.currentPosition * 1.0 / getDuration() * 100).toInt(), currentPlayMsgKey)
                }
            }
        }, 0, 500)
    }

    /**
     * 获取整段音频的长度
     */
    private fun getDuration(): Int {
        return try {
            mediaPlayer.duration
        } catch (e: IllegalStateException) {
            0
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        if (proximity) return

        if (currentPlayMsgKey != null) stop()

        voicePlayListener = null

        voiceMsgPlayEventListener = null
    }

    interface VoiceMsgPlayEventListener {

        fun onEvent(event: String, key: String?)
        fun onProgress(progress: Int, key: String?)
    }

    interface VoicePlayListener {
        fun onStart()
        fun onStop(findNextVoice: Boolean, msgKey: String?): Boolean
        fun acquireScreenOnLock()
        fun releaseScreenOnLock()
    }
}
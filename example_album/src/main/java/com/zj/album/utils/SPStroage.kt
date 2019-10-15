package com.zj.album.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.zj.album.AlbumAppContext

/**
 *
 * 存储变量到SP并操作
 *
 */
@SuppressLint("CommitPrefEdits")
object SPStroage{

    private const val SPName = "userInfo"
    private var sharedPreferences: SharedPreferences? = null

    private var editor: SharedPreferences.Editor? = null

    init {
        if (AlbumAppContext.appContext != null){
            sharedPreferences = AlbumAppContext.appContext!!.getSharedPreferences(SPName, Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
        }
    }

    // 耳筒模式开关
    private const val EARPIECE_MODE = "earpiece_mode"

    private val needClearProperty = arrayOf(EARPIECE_MODE)

    var earpieceMode: Boolean
        @JvmStatic
        get() = if(sharedPreferences == null) false else sharedPreferences!!.getBoolean(EARPIECE_MODE, false)
        @JvmStatic
        set(isOpen) {
            if (editor == null) return
            editor!!.putBoolean(EARPIECE_MODE, isOpen)
            editor!!.commit()
        }

    /**
     * 删除指定的属性值
     */
    fun removeProperty() {
        if (editor == null || needClearProperty.isEmpty()) return
        for (variableName in needClearProperty) {
            editor!!.remove(variableName)
            editor!!.commit()
        }

    }
}
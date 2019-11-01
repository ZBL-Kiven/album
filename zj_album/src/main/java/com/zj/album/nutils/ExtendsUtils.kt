@file:Suppress("unused")

package com.zj.album.nutils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import com.zj.album.BuildConfig
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @author ZJJ on 2019.10.24
 * */
fun <T> runWithTryCatch(block: () -> T?): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T : Serializable> Intent.getValueBySafe(name: String, default: T): T {
    return if (hasExtra(name)) {
        return try {
            when (T::class.java) {
                java.lang.Integer::class.java, Int::class.java -> {
                    getIntExtra(name, (default as? Int) ?: 0) as? T
                }
                java.lang.String::class.java, String::class.java -> {
                    getStringExtra(name) as? T
                }
                java.lang.Float::class.java, Float::class.java -> {
                    getFloatExtra(name, (default as? Float) ?: 0f) as? T
                }
                java.lang.Double::class.java, Double::class.java -> {
                    getDoubleExtra(name, (default as? Double) ?: 0.0) as? T
                }
                java.lang.Long::class.java, Long::class.java -> {
                    getLongExtra(name, (default as? Long) ?: 0L) as? T
                }
                java.lang.Boolean::class.java, Boolean::class.java -> {
                    getBooleanExtra(name, (default as? Boolean) ?: false) as? T
                }
                else -> {
                    throw IllegalArgumentException("Intent.getValueBySafe only supported by type : Boolean, Int, string, Float, Double, Long")
                }
            } ?: default
        } catch (e: Exception) {
            e.printStackTrace()
            default
        }
    } else default
}

inline fun <reified T : Serializable> Bundle.getValueBySafe(name: String, default: T): T {
    return if (containsKey(name)) {
        return try {
            when (T::class.java) {
                Int::class.java -> {
                    getInt(name, (default as? Int) ?: 0) as? T
                }
                String::class.java -> {
                    getString(name) as? T
                }
                Float::class.java -> {
                    getFloat(name, (default as? Float) ?: 0f) as? T
                }
                Double::class.java -> {
                    getDouble(name, (default as? Double) ?: 0.0) as? T
                }
                Long::class.java -> {
                    getLong(name, (default as? Long) ?: 0L) as? T
                }
                Serializable::class.java -> {
                    getSerializable(name) as? T
                }
                else -> {
                    throw IllegalArgumentException("Intent.getValueBySafe only supported by type : Int, string, Float, Double, Long")
                }
            } ?: default
        } catch (e: Exception) {
            e.printStackTrace()
            default
        }
    } else default
}

fun log(s: String) {
    if (BuildConfig.DEBUG) Log.e("ZJJ ----- ", s)
}

fun getDuration(mediaDuration: Long): String {
    val duration = mediaDuration / 1000
    val minute = duration / 60
    val second = duration % 60
    return String.format(Locale.getDefault(), "${if (minute < 10) "0%d" else "%d"}:${if (second < 10) "0%d" else "%d"}", minute, second)
}

fun <T : Any?> getPointIndexItem(collection: List<T>?, index: Int): T? {
    return if (index in 0 until (collection?.size ?: 0)) collection?.get(index) else null
}

fun showOrHideView(v: View?, isShow: Boolean, range: FloatArray, duration: Long) {
    if (v?.animation != null) v.clearAnimation()
    if (duration > 0) {
        if ((isShow && v?.visibility == View.VISIBLE) || (!isShow && v?.visibility == View.GONE)) return
        val anim = AlphaAnimation(range[0], range[1])
        anim.duration = duration
        v?.startAnimation(anim)
    }
    v?.visibility = if (isShow) View.VISIBLE else View.GONE
}
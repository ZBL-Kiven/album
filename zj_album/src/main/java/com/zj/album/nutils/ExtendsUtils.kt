@file:Suppress("unused")

package com.zj.album.nutils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.zj.album.BuildConfig
import java.io.Serializable
import java.lang.IllegalArgumentException

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
                Int::class.java -> {
                    getIntExtra(name, (default as? Int) ?: 0) as? T
                }
                String::class.java -> {
                    getStringExtra(name) as? T
                }
                Float::class.java -> {
                    getFloatExtra(name, (default as? Float) ?: 0f) as? T
                }
                Double::class.java -> {
                    getDoubleExtra(name, (default as? Double) ?: 0.0) as? T
                }
                Long::class.java -> {
                    getLongExtra(name, (default as? Long) ?: 0L) as? T
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

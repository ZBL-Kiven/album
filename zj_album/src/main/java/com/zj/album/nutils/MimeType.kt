@file:Suppress("unused", "SpellCheckingInspection")

package com.zj.album.nutils

/**
 * @author ZJJ on 2019.10.24
 * */
enum class MimeType(val mMimeTypeName: String, val mExtensions: Set<String>, val type: Int) {
    // ============== images ==============
    JPEG("image/jpeg", hashSetOf("jpg", "jpeg"), TYPE_IMG),
    PNG("image/png", hashSetOf("png"), TYPE_IMG),
    GIF("image/gif", hashSetOf("gif"), TYPE_IMG),
    BMP("image/x-ms-bmp", hashSetOf("bmp"), TYPE_IMG),
    WEBP("image/webp", hashSetOf("webp"), TYPE_IMG),

    // ============== videos ==============
    MPEG("video/mpeg", hashSetOf("mpeg", "mpg"), TYPE_VIDEO),
    MP4("video/mp4", hashSetOf("mp4", "m4v"), TYPE_VIDEO),
    QUICKTIME("video/quicktime", hashSetOf("mov"), TYPE_VIDEO),
    THREEGPP("video/3gpp", hashSetOf("3gp", "3gpp"), TYPE_VIDEO),
    THREEGPP2("video/3gpp2", hashSetOf("3g2", "3gpp2"), TYPE_VIDEO),
    MKV("video/x-matroska", hashSetOf("mkv"), TYPE_VIDEO),
    WEBM("video/webm", hashSetOf("webm"), TYPE_VIDEO),
    TS("video/mp2ts", hashSetOf("ts"), TYPE_VIDEO),
    AVI("video/avi", hashSetOf("avi"), TYPE_VIDEO);
}

internal const val TYPE_IMG = 1
internal const val TYPE_VIDEO = 2

internal fun isImage(mimeType: String?): Boolean {
    return mimeType?.startsWith("image") ?: false
}

internal fun isVideo(mimeType: String?): Boolean {
    return mimeType?.startsWith("video") ?: false
}

internal fun isGif(mimeType: String?): Boolean {
    return mimeType == MimeType.GIF.mMimeTypeName
}

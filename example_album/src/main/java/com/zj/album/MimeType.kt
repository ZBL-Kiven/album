/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zj.album

import java.util.*

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 *
 *
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
enum class MimeType(val mMimeTypeName: String, val mExtensions: Set<String>) {

    // ============== images ==============
    JPEG("image/jpeg", hashSetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", hashSetOf(
            "png"
    )),
    GIF("image/gif", hashSetOf(
            "gif"
    )),
//    BMP("image/x-ms-bmp", hashSetOf(
//            "bmp"
//    )),
//    WEBP("image/webp", hashSetOf(
//            "webp"
//    )),

    // ============== videos ==============
    MPEG("video/mpeg", hashSetOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", hashSetOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("video/quicktime", hashSetOf(
            "mov"
    )),
    THREEGPP("video/3gpp", hashSetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", hashSetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", hashSetOf(
            "mkv"
    )),
    WEBM("video/webm", hashSetOf(
            "webm"
    )),
    TS("video/mp2ts", hashSetOf(
            "ts"
    )),
    AVI("video/avi", hashSetOf(
            "avi"
    ));

    companion object {

        fun ofAll(): EnumSet<MimeType> {
            return EnumSet.allOf(MimeType::class.java)
        }

        fun of(type: MimeType, vararg rest: MimeType): EnumSet<MimeType> {
            return EnumSet.of(type, *rest)
        }

        fun ofImage(): EnumSet<MimeType> {
            return EnumSet.of(JPEG, PNG, GIF)
        }

        fun ofStaticImage(): EnumSet<MimeType> {
            return EnumSet.allOf(MimeType::class.java).apply { remove(GIF) }
        }

        fun ofVideo(): EnumSet<MimeType> {
            return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI)
        }

        fun isImage(mimeType: String?): Boolean {
            return mimeType?.startsWith("image") ?: false
        }

        fun isVideo(mimeType: String?): Boolean {
            return mimeType?.startsWith("video") ?: false
        }

        fun isGif(mimeType: String?): Boolean {
            return if (mimeType == null) false else mimeType == MimeType.GIF.toString()
        }
    }

}

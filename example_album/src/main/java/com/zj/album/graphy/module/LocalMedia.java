package com.zj.album.graphy.module;

import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * Created by zhaojie on 2017/10/19.
 */

public class LocalMedia {

    public String uri;
    public boolean isSelector;
    public int index;
    public String mime;
    public long duration;

    public LocalMedia() {
    }

    public LocalMedia(String uri, boolean isSelector) {
        this.uri = uri;
        this.isSelector = isSelector;
    }

    public static LocalMedia valueOf(Cursor cursor) {
        LocalMedia localMedia = new LocalMedia();
        localMedia.uri = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
        localMedia.mime = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
        if (localMedia.isVideo()) {
            localMedia.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
        }
        return localMedia;
    }

    public String getFileUri(){
        return "file://" + uri;
    }

    public boolean isVideo(){
        return !TextUtils.isEmpty(mime) && mime.contains("video");
    }
}

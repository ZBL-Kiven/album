package com.zbl.album.omnitpotent.interfaces;

import android.content.Intent;

/**
 * @author yangji
 */
public interface ResultListener {
    void success(Intent data);

    void cancel();
}

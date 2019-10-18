package com.zbl.album;

import android.content.Intent;

import com.zbl.album.omnitpotent.interfaces.ResultListener;

/**
 * @author yangji
 */
public abstract class AlbumListener implements ResultListener {

    @Override
    public final void success(Intent data) {
        boolean fullSize = data.getExtras().getBoolean("fullSize");
        success(fullSize);
    }

    protected abstract void success(boolean fullSize);


}

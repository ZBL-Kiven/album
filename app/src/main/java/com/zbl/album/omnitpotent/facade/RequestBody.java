package com.zbl.album.omnitpotent.facade;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author yangji
 */
public class RequestBody {

    private Class<? extends Activity> cls;

    private Bundle bundle = new Bundle();

    public Class<? extends Activity> getCls() {
        return cls;
    }

    public void setCls(Class<? extends Activity> cls) {
        this.cls = cls;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

}

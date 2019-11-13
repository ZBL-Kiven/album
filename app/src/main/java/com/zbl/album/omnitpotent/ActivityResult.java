package com.zbl.album.omnitpotent;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.zbl.album.omnitpotent.facade.RequestBody;
import com.zbl.album.omnitpotent.interfaces.ResultListener;

/**
 * @author yangji
 */
public class ActivityResult {

    private RequestBody body;
    private final FragmentManager mFragmentManager;

    private ActivityResult(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    public static ActivityResult with(FragmentActivity activity) {
        return new ActivityResult(activity.getSupportFragmentManager());
    }

    public static ActivityResult with(Fragment fragment) {
        return new ActivityResult(fragment.getChildFragmentManager());
    }


    public ActivityResult activity(Class<? extends Activity> cls) {
        if (body == null) {
            body = new RequestBody();
        }
        body.setCls(cls);
        return this;
    }

    public ActivityResult bundle(Bundle b) {
        if (body == null) {
            body = new RequestBody();
        }
        body.setBundle(b);
        return this;
    }

    public void build(ResultListener listener) {
        if (body == null || body.getCls() == null) {
            throw new NullPointerException("Activity 未指定");
        }
        ResultFragment fragment = ResultFragment.getInstance(body, listener);
        mFragmentManager
                .beginTransaction()
                .add(fragment, ActivityResult.class.getName())
                .commitNow();
    }
}

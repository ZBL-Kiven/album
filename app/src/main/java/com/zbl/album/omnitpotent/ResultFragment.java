package com.zbl.album.omnitpotent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.zbl.album.omnitpotent.facade.RequestBody;
import com.zbl.album.omnitpotent.interfaces.ResultListener;

/**
 * @author yangji
 */
public class ResultFragment extends Fragment {

    private static final String TAG = "ResultFragment";
    private ResultListener mListener;
    private RequestBody mRequestBody;
    private int requestId;

    public void setRequestBody(RequestBody body) {
        this.mRequestBody = body;
    }

    static ResultFragment getInstance(RequestBody body, ResultListener listener) {
        ResultFragment frt = new ResultFragment();
        frt.setListener(listener);
        frt.setRequestBody(body);
        return frt;
    }

    private void setListener(ResultListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        requestId = RequestId.getRequestId();
        Intent intent = new Intent(this.getContext(), mRequestBody.getCls());
        if (mRequestBody.getBundle() != null) {
            intent.putExtras(mRequestBody.getBundle());
        }
        startActivityForResult(intent, requestId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == requestId) {
            mListener.success(data);
        } else {
            mListener.cancel();
        }
        remove();
    }

    private void remove() {
        FragmentManager fragmentManager;
        if (getParentFragment() == null && getActivity() != null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        } else {
            fragmentManager = getParentFragment().getChildFragmentManager();
        }
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().remove(this).commitNow();
            Log.e(TAG, "自我回收成功");
        } else {
            Log.e(TAG, "无法自我删除了");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}

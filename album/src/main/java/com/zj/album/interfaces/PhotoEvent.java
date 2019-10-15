package com.zj.album.interfaces;

/**
 * Created by zhaojie on 2018/2/5.
 */

public interface PhotoEvent {
    /**
     * @param isValidate 该操作是否有效
     * @param code       数据区分
     */
    void onEvent(int code, boolean isValidate);
}
